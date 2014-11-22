使用说明：
方式1、./WordSplit.sh input dict1 dict2 . . dictn
-参数1：分词的标准文本语料的路径
-参数2：分词的词典1的路径
-参数3：分词的词典2的路径
-参数x：分词的词典x的路径
-参数n：分词的词典n的路径
最终分词结果文本是input.seg
注意：分词词典、原始语料的编码格式只支持GBK。

方式2、分词（./WordSplit vocab input output）
-参数1：vocab指分词词典文件路径
-参数2：input指原始语料文件路径
-参数2：output指分词结果文件路径
注意：分词词典、原始语料的编码格式只支持GBK。

