#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "Dict.h"
#include "FindLetterAndNumber.h"

int GetElem(char* szLine)
{
	int nElem = 0;
	int lastchartype = UNKNOWN;
	for (int i=0;szLine[i]!=0;)
	{
		int nStep = JustGetStepSize(szLine+i);
		int chartype = WhichChar(szLine+i);
		if(chartype == ZIMU && lastchartype == ZIMU)
			nElem --;
		i += nStep;
		nElem++;
		lastchartype = chartype;
	}
	return nElem;
}

CDictionary::CDictionary()
{
	m_pDict = NULL;
	m_Hash_Dict = NULL;
	m_pWordBuff = NULL;
}

CDictionary::~CDictionary()
{
	if(m_pDict)
		delete[] m_pDict;
	if(m_Hash_Dict)
		delete m_Hash_Dict;
	if(m_pWordBuff)
		delete[] m_pWordBuff;
}


int CDictionary::Init(char* dictfile)
{
	FILE* fp = fopen(dictfile,"r");
	if(fp == NULL)
	{
		printf("can't open %s\n",dictfile);
		return -1;
	}
	char szLine[1024];
	m_nDict = 0;
	while(fgets(szLine,1024,fp))
	{
		m_nDict++;
	}
	m_nWordBuff = ftell(fp)+1;
	fseek(fp,0,SEEK_SET);

	if(m_nDict == 0)
	{
		printf("no line in %s\n",dictfile);
		return -1;
	}
	m_pWordBuff = new char[m_nWordBuff];
	m_pDict = new dict_t[m_nDict];
	m_Hash_Dict = new StringHash(2*m_nDict);
	m_nMaxDictWordLen = 0;
	int id = 0;
	char* p = m_pWordBuff;
	while(fgets(p,1024,fp))
	{
		int len = strlen(p);
		if(len > 0 && p[len - 1] == '\n')
		{
			p[len - 1] = '\0';
			if(len > 1 && p[len -2] == '\r')
				p[len-2] = 0;
		}
		
		float weight = 1.0f;
		char* p2 = strchr(p,'\t');
		if(p2 != NULL)
		{
			*p2 = 0;
			p2 ++;
			weight = atof(p2);
		}
		m_pDict[id].word = p;
		int nElem = GetElem(p);
		m_pDict[id].nElem = nElem;
		if(m_nMaxDictWordLen < nElem)
			m_nMaxDictWordLen = nElem;
		m_pDict[id].fWeight = weight*(nElem - 1.0f/(nElem+10.f));
		
		if(!m_Hash_Dict->Add(p,id))
		{
			printf("Add [%s] to hashtable failed\n",p);
			return -1;
		}
		
		p += strlen(p) + 1;
		id++;
	}

	fclose(fp);
	return 0;
}
