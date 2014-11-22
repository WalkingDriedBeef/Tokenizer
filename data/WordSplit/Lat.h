#ifndef _LAT_H_
#define _LAT_H_

#include "Dict.h"

#define MAX_TRANS_NUM 20
#define MAX_NODES_NUM 512
#define MAX_WORD_LEN 28

struct trans_t
{
	int from_node;
	int to_node;
	float weight;
	int is_unknown;
	union
	{
		int word_id;
		char word[MAX_WORD_LEN];
	};
	trans_t* pNext;
};

struct trace_t
{
	int node_id;
	trans_t* hist;
	float fscore;
};

struct node_t
{
	int depth;
	trans_t from_trans[MAX_TRANS_NUM];
	int from_trans_num;
	trans_t to_trans[MAX_TRANS_NUM];
	int to_trans_num;
	float fFwdScore;
};

class Lat
{
private:
	node_t* nodes;
	int max_node_num;
	int node_num;
	int*  nodeid_vec;

	char* term_vec;
	int term_vec_len;

	int depth;

	CDictionary* dict;
	dict_t*	m_words;
	int max_dictwordlen;
	
private:
	int GetBestPath(int nodeid,float& fBestScore,trans_t** bestpath);
public:
	Lat();
	~Lat();

	int SplitWord(char* word,char* split_word);
	int SplitWord_File(char* input,char* output);
	void SetDict(CDictionary* vocab){dict = vocab;m_words = dict->GetDict();max_dictwordlen = dict->GetMaxDictWordLen();}
};
#endif