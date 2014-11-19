#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "Lat.h"
#include "FindLetterAndNumber.h"

Lat::Lat()
{
	max_node_num = MAX_NODES_NUM;
	nodes = new node_t[max_node_num];
	nodeid_vec = new int[max_node_num];
	dict = NULL;
	term_vec_len = MAX_WORD_LEN*max_node_num;
	term_vec = new char[term_vec_len];
}

Lat::~Lat()
{
	if(nodes)
	{
		delete[] nodes;
	}
	if(term_vec)
		delete[] term_vec;
	if(nodeid_vec)
		delete[] nodeid_vec;
}

int Lat::SplitWord(char* word,char* split_word)
{
	if (word == NULL || word[0] == 0 || word[0] == '\n')
	{
		split_word[0] = 0;
		return 0;
	}
	
	//拆成单字串
	int len = strlen(word);
	if(max_node_num < len+1)
	{
		max_node_num = len+1;
		delete[] nodes;
		nodes = new node_t[max_node_num];
		delete[] nodeid_vec;
		nodeid_vec = new int[max_node_num];
		delete[] term_vec;
		term_vec_len = MAX_WORD_LEN*max_node_num;
		term_vec = new char[term_vec_len];
	}

	node_num = 0;
	char* term;
	trans_t tmp_tran;

	nodes[0].fFwdScore = 0;
	for (int i = 0; i < len;  )
	{
		int nStep;
		int step_sum = 0;
		do 
		{
			nStep = JustGetStepSize(word+i+step_sum);
			int chartype = WhichChar(word+i+step_sum);
			if(chartype == ZIMU)
			{
				step_sum += nStep;
			}
			else
			{
				if(step_sum != 0)	//说明有连续字母
				{
					nStep = step_sum;
				}
				break;
			}
		} while (i < len && step_sum < MAX_WORD_LEN);

		term = term_vec + MAX_WORD_LEN*node_num;

		for (int j = 0; j< nStep; j++)
		{
			term[j] = word[i+j];
		}
		term[nStep] = 0;

		tmp_tran.from_node = node_num;
		tmp_tran.to_node = node_num + 1;
		int word_id = dict->Find(term);

		//单字权重
		if(word_id == -1)
		{
			tmp_tran.weight = 0.8f*(1 - 1.0f/(1+10.f));
			strcpy(tmp_tran.word,term);
			tmp_tran.is_unknown = 1;
		}
		else
		{
			tmp_tran.word_id = word_id;
			tmp_tran.weight = m_words[word_id].fWeight;
			tmp_tran.is_unknown = 0;
		}
		
		nodes[node_num].depth = node_num;
		nodes[node_num].from_trans_num = 1;
		nodes[node_num].from_trans[0] = tmp_tran;

		node_num++;
		nodes[node_num].depth = node_num;
		nodes[node_num].to_trans_num = 1;
		nodes[node_num].to_trans[0] = tmp_tran;
		nodes[node_num].fFwdScore = -10000.f;
		
		i += nStep;
	}
	nodes[0].to_trans_num = 0;
	nodes[node_num].from_trans_num = 0;
	node_num++;

	//连词
	for (int i = 0; i< node_num - 2; i++)
	{
		char szTmp[1024];
		strcpy(szTmp,&term_vec[MAX_WORD_LEN*i]);
		int j = i;
		do
		{
			j++;
			strcat(szTmp,&term_vec[MAX_WORD_LEN*j]);
			if(nodes[j+1].depth <= nodes[i].depth)
			{
				continue;
			}
			int word_id = dict->Find(szTmp);
			if(word_id != -1)
			{
				tmp_tran.from_node = i;
				tmp_tran.to_node = j+1;
				tmp_tran.word_id = word_id;
				tmp_tran.is_unknown = 0;
				tmp_tran.weight = m_words[word_id].fWeight;

				nodes[tmp_tran.from_node].from_trans[nodes[tmp_tran.from_node].from_trans_num++] = tmp_tran;
				nodes[tmp_tran.to_node].to_trans[nodes[tmp_tran.to_node].to_trans_num++] = tmp_tran;
				if(nodes[tmp_tran.to_node].depth > nodes[tmp_tran.from_node].depth + 1)
				{
					nodes[tmp_tran.to_node].depth = nodes[tmp_tran.from_node].depth + 1;
				}
			}		
		}while(j < node_num -2 && j - i < max_dictwordlen);
	}

	//更新depth
	for(int i = 0; i< node_num; i++)
	{
		for (int j = 0; j< nodes[i].from_trans_num; j++)
		{
			int to_node = nodes[i].from_trans[j].to_node;
			if(nodes[to_node].depth > nodes[i].depth + 1)
			{
				nodes[to_node].depth = nodes[i].depth + 1;
			}
		}
	}
	
	//查找1+n+1的结构，让1+1+n或n+1+1优于1+n+1,n>1
	for (int i = node_num-1; i>=3; i--)
	{
		for (int j = i -4; j >= 0;j--)
		{
			//必须入弧唯一，才会可能是单字
			if(nodes[j+1].to_trans_num > 1)
				continue;
			trans_t* mid = NULL;
			//寻找1+n+1
			for(int k = nodes[j+1].from_trans_num -1; k> 0 ;k--)
			{
				if(nodes[j+1].from_trans[k].to_node == i-1 && nodes[i-1].from_trans_num == 1)
				{
					mid = &nodes[j+1].from_trans[k];
					break;
				}
			}
			if(mid != NULL)
			{
				//1+1+n+m
				for(int k = nodes[j+2].from_trans_num -1; k> 0 ;k--)
				{
					if(nodes[j+2].from_trans[k].to_node == i)
					{
						if(i == node_num-1 || nodes[i].from_trans_num > 1)
						{
							mid->weight += -0.05f;
							mid = NULL;
						}
						break;
					}
				}
			}
			if(mid != NULL)
			{
				//n+1+1
				for(int k = nodes[j].from_trans_num -1; k> 0 ;k--)
				{
					if(nodes[j].from_trans[k].to_node == i-2)
					{
						if(j == 0 || nodes[j-1].to_trans_num > 1)
						{
							mid->weight += -0.05f;
							mid = NULL;
						}
						break;
					}
				}
			}
		}
	}

	//逆向寻找最优路径
	//先计算前向得分
	for (int i= 0; i< node_num; i++)
	{
		for (int j = nodes[i].from_trans_num - 1; j>= 0; j-- )
		{
			int to_node = nodes[i].from_trans[j].to_node;
			if(nodes[i].depth < nodes[to_node].depth)
			{
				float newscore = nodes[i].fFwdScore + nodes[i].from_trans[j].weight;
				if(newscore > nodes[to_node].fFwdScore)
					nodes[to_node].fFwdScore = newscore;
			}
		}
	}

	int split_num = nodes[node_num-1].depth + 1;
	nodeid_vec[0] = node_num-1;
	nodeid_vec[split_num-1] = 0;

	float bestscore = -10000.0f;
	trans_t* bestpath = NULL;
	trace_t trace;
	trace.node_id = node_num-1;
	trace.fscore = 0.0f;
	trace.hist = NULL;

	int cur_node = node_num-1;
	trans_t* pre_hist = NULL;
	
	while (cur_node != 0)
	{
		float fBestScore = 0.f;
		int BestTrans = -1;
		for (int i = nodes[cur_node].to_trans_num - 1; i>= 0; i-- )
		{
			int from_node = nodes[cur_node].to_trans[i].from_node;
			if(nodes[from_node].depth <= nodes[cur_node].depth - 1)
			{
				float newscore = nodes[from_node].fFwdScore + nodes[cur_node].to_trans[i].weight;
				if(newscore < nodes[cur_node].fFwdScore)
				{
				}
				else
				{
					BestTrans = i;
				}
			}
		}
		if(BestTrans == -1)
		{
			BestTrans = nodes[cur_node].to_trans_num - 1;
			printf("Warning:Failed Parse in [%s]\n",word);
		}
		nodes[cur_node].to_trans[BestTrans].pNext = pre_hist;
		pre_hist = &nodes[cur_node].to_trans[BestTrans];
		cur_node = nodes[cur_node].to_trans[BestTrans].from_node;
	}

	trans_t* head = pre_hist;
	split_word[0] = 0;
	while(head != NULL)
	{
		if(head->is_unknown == 1)
		{
			strcat(split_word,head->word);
		}
		else
		{
			strcat(split_word,m_words[head->word_id].word);
		}
		strcat(split_word," ");
		head = head->pNext;
	}
	return 0;
}

int Lat::SplitWord_File(char* input,char* output)
{
	FILE* fp = fopen(input,"r");
	if(fp == NULL)
	{
		printf("can't open %s\n",input);
		return -1;
	}
	FILE* fpSave = fopen(output,"w");
	if(fpSave == NULL)
	{
		printf("can't open %s\n",output);
		return -1;
	}
	char szLine[10240];
	char szLine_split[2*10240];
	int nID = 0;
	while(fgets(szLine,10240,fp))
	{
		szLine_split[0] = 0;
		char *pLine = szLine;
		char *pLine_split = szLine_split;
		for (int i = 0; ; i++)
		{
			if(szLine[i] == ' ' || szLine[i] == '\t' )
			{
				szLine[i] = 0;
				if(pLine[0] != 0)
				{
					if(0 != SplitWord(pLine,pLine_split))
					{
						printf("Warn:Seg[%s] failed\n",szLine);
					}
					else
					{
						int add_len = strlen(szLine_split);
						pLine_split = szLine_split + add_len;
					}
				}
				pLine = szLine + i+1;

			}
			else if(szLine[i] == '\n' || szLine[i] == '\0' || szLine[i] == '\r')
			{
				szLine[i] = 0;
				if(pLine[0] != 0)
				{
					if(0 != SplitWord(pLine,pLine_split))
					{
						printf("Warn:Seg[%s] failed\n",szLine);
					}
					else
					{
						int add_len = strlen(szLine_split);
						pLine_split = szLine_split + add_len;
					}
				}
				pLine = szLine + i+1;
				break;
			}
		}
		
		int len = strlen(szLine_split);
		if(len > 0)
		{
			if(szLine_split[len-1] == ' ')
				szLine_split[len-1] = 0;
		}
		fprintf(fpSave,"%s\n",szLine_split);
		
		if(++nID%10000 == 0)
			printf("\rDealing Line %d",nID);
	}

	fclose(fpSave);
	fclose(fp);

	printf("Split Success[total=%d]!\n",nID);
	return 0;
}

