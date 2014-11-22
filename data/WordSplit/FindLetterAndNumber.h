#ifndef _HEADER_H_
#define _HEADER_H_

#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

bool TTSIsShuZiChar(const char *szBuf);
bool TTSIsSpaceChar(const char *szBuf);
bool TTSIsZiMuChar(const char *szBuf);
int JustGetStepSize(const char *szBuf);
bool TTSIsHanZiChar(const char *szBuf);
bool TTSIsGB2312HanZiChar(const char *szBuf);
char * ConvertBanjiao2Quanjiao(char * szQuanjiao, char * szBanjiao);
char * ConvertQuanjiao2Banjiao(char *szBanjiao, char *szQuanjiao);
int WhichChar(const char *szBuf);

enum CharType{SHUZI,SPACE,ZIMU,HANZI,UNKNOWN};

struct RepChar
{
	char Src;
	char Dst;
};
struct CharOperater
{
	int DelLine ;
	int KeepZimu ;
	int KeepHanzi ;
	int KeepSpace ;
	int KeepShuZi ;
	int KeepElse;
	int QuanToBan ;
	int nReplace;
	RepChar cRpl;
};
//返回1表示修改了该buf,返回了0表示没有修改buf,返回了-1表示删除了该行
int OperaterChar(CharOperater Oper,const char *szbuf, int nStep,int nCharType,char *pDest);

#endif