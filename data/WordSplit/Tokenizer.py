#!/usr/bin/env python
# encoding=gbk
import os
import sys

G_ENCODE = "GBK"

class Dct():
    hashMap = {}
    maxlen = 0
    def __init__(self, cache):
        if os.path.isfile(cache):
            cacheF = open(cache)
            for line in cacheF:
                line = line.strip()
                if line.find("\t") > -1:
                    try:
                        self.hashMap[line.split("\t")[0]] = float(line.split("\t")[1])
                    except:
                        self.hashMap[line.split("\t")[0]] = 0.8
                    self.maxlen = len(line.split("\t")[1]) if len(line.split("\t")[1]) > self.maxlen else self.maxlen
                else:
                    self.hashMap[line] = 0.8
                    self.maxlen = len(line) if len(line) > self.maxlen else self.maxlen
            cacheF.close()
        else:
            print "No cache found!"
class Node: 
    def __init__(self, data, index_from, index_to, weight, isLeaf, isSig):
        self.data = data
        self.index_from = index_from
        self.index_to = index_to
        self.weight = weight
        self.isLeaf = isLeaf
        self.isSig = isSig

class Tokenizer():
    def segmenter(self, dct, sent):
        lats = {}
        usent = unicode(sent, G_ENCODE)
        for i in range(0, len(usent)):
            j = 0
            while j < dct.maxlen and i + j < len(usent):
                j += 1
                tmp = usent[i: i + j].encode(G_ENCODE)
                if dct.hashMap.has_key(tmp) or j == 1:
                    isLeaf = True if i + j == len(usent) else False
                    isSig = 1 if j == 1 else 0
                    weight = dct.hashMap.get(tmp) if dct.hashMap.get(tmp) != None else 0.8
                    node = Node(tmp, i, i + j, weight, isLeaf, isSig)
                    if lats.has_key(i):
                        val = lats.get(i)
                        val.append(node)
                        lats[i] = val
                    else:
                        lats[i] = [node]
        # for i in lats.keys():
        #    nodes = lats.get(i)
        #    for node in nodes:
        #        print "key: [%s] -- %s - %s - %s - %s - %s - %s"%(i, node.data, node.index_from, node.index_to, node.weight, node.isLeaf, node.isSig)
        bestpath = {"bestpath":"", "score":0.0}
        self.getBestPath(lats, bestpath, len(usent))
        print bestpath.get("bestpath"), bestpath.get("score")
        bestSegPath = bestpath.get("bestpath")

        return " ".join(sent) if bestSegPath == "" else bestSegPath

    """
        get best path [calculate the path's score]:
        1. 统计路径的所有单词的权重得分 [WEIGHTs / DEPTH]  * 50
        2. 统计路径的所有单字词数量得分 [DEPTH - SIGWN / DEPTH] * 15
        3. 统计路径的深度得分           [sentLen - DEPTH / sentLen] * 35
    """
    def getBestPath(self, lats, bestpath, sentLen, NODE=Node("<R>", 0, 0, 0.0, False, 0), PATH="", WEIGHT=0.0, DEPTH=0, ISSIG=0):
        if lats == None or NODE == None or sentLen == 0:
            print "Waining: get best path exception!"
            return None
        if NODE.isLeaf:
            path_total = 0
            if DEPTH > 0:
                path_weight = round(WEIGHT * 0.5 / DEPTH, 6)
                path_sigwei = 0.0 if round((DEPTH - ISSIG) * 0.15 / DEPTH, 6) < 0 else round((DEPTH - ISSIG) * 0.15 / DEPTH, 6)
                path_depwei = 0.0 if round((sentLen - DEPTH) * 0.35 / sentLen, 6) < 0 else round((sentLen - DEPTH) * 0.35 / sentLen, 6)
                path_total = path_weight + path_sigwei + path_depwei

            if bestpath == None or bestpath == {}:
                bestpath = {"bestpath": PATH, "score":path_total}
            else:
                if path_total > bestpath.get("score"):
                    bestpath["score"] = path_total                
                    bestpath["bestpath"] = PATH.strip()
            # print "%s---[%s]---[%s]---[%s]---[%s]"%(PATH.strip(), WEIGHT, DEPTH, ISSIG, path_total)
            PATH = ""
            WEIGHT = 0.0
            DEPTH = 0
            ISSIG = 0
        else:
            children = lats.get(NODE.index_to)
            if children != None:
                DEPTH += 1
                for child in children:
                    self.getBestPath(lats, bestpath, sentLen, NODE=child, PATH=PATH + " " + child.data, WEIGHT=WEIGHT + child.weight, DEPTH=DEPTH, ISSIG=ISSIG + child.isSig)


sent = "笔记本电脑笔记本电脑"
print len(unicode(sent, "gbk"))
dct = Dct("../data/words.dict.bak")
tokenizer = Tokenizer()
tokenizer.segmenter(dct, sent)
"""
def SegFile(dict, cache, outcache):
    if not os.path.isfile(dict):
        print "Error: cache not found! - [%s]" % (dict)
        sys.exit(-1)
    if not os.path.isfile(cache):
        print "Error: cache not found! - [%s]" % (cache)
        sys.exit(-1)
    i_cache = open(cache)
    o_cache = open(outcache, "w")
    dct = Dct(dict)
    tokenizer = Tokenizer()

    for sent in i_cache:
        res = tokenizer.segmenter(dct, sent.strip())
        o_cache.write(res + "\n")

    i_cache.close()
    o_cache.close()
import datetime
if __name__ == '__main__':
#     if len(sys.argv) != 4:
#         print "Usage: %s dict[in] input[in] output[in]."%(sys.argv[0])
#         sys.exit(-1)
#     now = datetime.datetime.now()
#     print now.strftime('%Y-%m-%d %H:%M:%S')
#     SegFile(sys.argv[1], sys.argv[2], sys.argv[3])
#     print now.strftime('%Y-%m-%d %H:%M:%S')
    SegFile("../data/words.dict.bak", "../data/RMRB.head200w.low", "../data/Seg.out")

"""