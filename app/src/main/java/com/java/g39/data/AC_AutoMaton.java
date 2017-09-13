package com.java.g39.data;

import java.util.*;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

/**
 * Created by 岳 on 2017/9/13.
 */


public class AC_AutoMaton{
    private class KeyValue
    {
        public KeyValue(String k,int v)
        {
            key=k;
            value=v;
        }
        String key;
        Integer value;
    }
    private Trie trie = new Trie(false);//AC自动机
    private Map<String,Integer> map = new HashMap<String,Integer>();//文本向量

    //加入一个关键词-数量二元组
    public void add(String key,int value)
    {
        map.put(key, value);
        trie.addKeyword(key);
    }

    //查询文本中和AC自动机匹配的全部词条，并按优先级排序
    public List<String> find(String text)
    {
        List<String> result = new ArrayList<String>();
        Collection<Emit> emits = trie.parseText(text);
        List<KeyValue> sortList = new ArrayList<>();
        Set<String> set = new HashSet<String>();
        for(Emit e : emits)
        {
            String key=e.getKeyword();
            int v = map.get(e.getKeyword());
            if(!set.contains(key))
            {
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