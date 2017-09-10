package com.java.g39.data;

import android.util.Pair;

import java.util.*;

import static java.lang.Math.sqrt;

/**
 * Created by 岳 on 2017/9/10.
 * 新闻综合推荐系统
 *
 */

public class RecSystem {
    private static final RecSystem ourInstance = new RecSystem();

    public static RecSystem getInstance() {
        return ourInstance;
    }

    private RecSystem() {
    }

    final double readScore=1.0,favoriteScore=5.0,classScore=500.0;

    /**
     * 文本向量
     */
    private class ItemVector{
        Map<String,Double> vec; //特征向量
        public ItemVector()
        {
            vec = new HashMap<>();
        }
        public ItemVector(DetailNews news)
        {
            this();
            add(news,1,classScore);
        }

        /**
         * @param news 添加的新闻
         * @param weight 该新闻的权重
         * @param classWeight 类别的权重
         */
        public void add(DetailNews news,double weight,double classWeight)
        {
            for(DetailNews.WordWithScore p : news.Keywords)
            {
                if(!vec.containsKey(p.word))
                    vec.put(p.word,0.0);
                vec.put(p.word,vec.get(p.word)+p.score*weight);
            }
            if(!vec.containsKey(news.newsClassTag))
                vec.put(news.newsClassTag,0.0);
            vec.put(news.newsClassTag,vec.get(news.newsClassTag)+weight*classWeight);
        }

        /**
         * @return 特征向量的二范数
         */
        double normal2()
        {
            double result=0.0;
            for(double v : vec.values())
                result+=v*v;
            return sqrt(result);
        }
    }

    /**
     * @param a 文本向量a
     * @param b 文本向量a
     * @param normal1    a的二范数(加速)
     * @param normal2    b的二范数(加速)
     * @return 向量的余弦相似度
     */
    private double cosineSimilarity(ItemVector a,ItemVector b,double normal1,double normal2)
    {
        double result=0.0;
        for(String key : b.vec.keySet())
            if(a.vec.containsKey(key))
                result+=a.vec.get(key)*b.vec.get(key);
        return result/(normal1*normal2);
    }

    /**
     * @param source      候选新闻列表
     * @param read 已读新闻列表
     * @param favorite 喜好新闻列表
     * @return  按推荐排序后的候选新闻列表
     */
    public List<DetailNews> recommendSort(List<DetailNews> source,List<DetailNews> read,List<DetailNews> favorite)
    {
        List<DetailNews> result = new ArrayList<DetailNews>();
        ItemVector User = new ItemVector();
        for(DetailNews data:read)
            User.add(data,readScore,classScore);
        for(DetailNews data:favorite)
            User.add(data,favoriteScore,classScore);
        double lenUser = User.normal2();
        List<Pair<Double,DetailNews>> sortList = new ArrayList<>();
        for(DetailNews news : source) {
            ItemVector itemVector = new ItemVector(news);
            double lenItem = itemVector.normal2();
            double dist = cosineSimilarity(User, itemVector,lenUser,lenItem);
            sortList.add(new Pair<Double,DetailNews>(dist,news));
        }
        Collections.sort(sortList,new Comparator<Pair<Double,DetailNews>>(){
            public int compare(Pair<Double,DetailNews> arg0, Pair<Double,DetailNews> arg1) {
                return arg0.first.compareTo(arg1.first);
            }
        });
        return result;
    }


}
