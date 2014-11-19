#ifndef _DICT_
#define _DICT_

#include "Hash.h"

struct dict_t
{
	char* word;
	float fWeight;
	int   nElem;
};

class CDictionary
{
private:
	dict_t* m_pDict;
	int		m_nDict;
	StringHash* m_Hash_Dict;
	char*	m_pWordBuff;
	int		m_nWordBuff;
	int		m_nMaxDictWordLen;
public:
	CDictionary();
	~CDictionary();

	int Init(char* dictfile);
	int Find(char* str){return m_Hash_Dict->Find(str);}
	int Add(char* str,int id){return m_Hash_Dict->Add(str,id);}
	dict_t* GetDict(){return m_pDict;}
	int GetMaxDictWordLen(){return m_nMaxDictWordLen;}
};
#endif