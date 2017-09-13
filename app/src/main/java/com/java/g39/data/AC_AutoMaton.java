package com.java.g39.data;

import android.util.Log;

import java.util.*;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

/**
 * Created by 岳 on 2017/9/13.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

class AC_AutoMaton {
    private class KeyValue
    {
         KeyValue(String k,int v)
        {
            key=k;
            value=v;
        }
        String key;
        Integer value;
    }
    private AhoCorasick trie = new AhoCorasick();//AC自动机
    private Map<String,Integer> map = new HashMap<String,Integer>();//文本向量
    private List<String> keys = new ArrayList<>();//临时数组
    //加入一个关键词-数量二元组
    public void add(String key,int value)
    {
        final int VALUE2=1300;
        final int MIN_VALUE=10;
        if(value<=MIN_VALUE)return;
        if(key.length()<=2 && value<VALUE2)return;
        map.put(key, value);
        keys.add(key);
    }

    //构造fail指针
    public void fix()
    {
        String[] keys_ = new String[keys.size()];
        int tot=0;
        for(String s : keys)
            keys_[tot++]=s;
        trie.createTrie(keys_);
        trie.getFailure();
        keys=null;
    }

    //查询文本中和AC自动机匹配的全部词条，并按优先级排序
    public List<String> find(String text)
    {
        List<String> result = new ArrayList<String>();
        LinkedList<String> emits = trie.search(text,true);
        List<KeyValue> sortList = new ArrayList<>();
        Set<String> set = new HashSet<String>();
        for(String key : emits)
        {
            int v = map.get(key);
            if(!set.contains(key))
            {
                //Log.d("ACM",key+" "+String.format("%d",v));
                set.add(key);
                sortList.add(new KeyValue(key,v));
            }
        }
        Collections.sort(sortList,new Comparator<KeyValue>(){
            public int compare(KeyValue arg0, KeyValue arg1) {
                return arg0.value.compareTo(arg1.value);
            }
        });
        for(KeyValue p : sortList)
            result.add(p.key);
        return result;
    }
}