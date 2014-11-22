#include "Lat.h"
#include <stdio.h>
#include <string.h>

int main(int argc,char* argv[])
{
	if(argc != 4)
	{
		printf("Usage:%s vocab input output\n",argv[0]);
		return -1;
	}

	CDictionary dict;
	if(0 != dict.Init(argv[1]))
	{
		printf("Init dict faild\n");
		return -1;
	}

	Lat lat;
	lat.SetDict(&dict);
	lat.SplitWord_File(argv[2],argv[3]);
	return 0;
}