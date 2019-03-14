package com.example.robin.papers.studentCircle.tools;

import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.studentCircle.adapter.CollectionListAdapter;
import com.example.robin.papers.studentCircle.adapter.DynamicListAdapter;
import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsFromCommentActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCollectionActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCommentActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyDynamicActivity;

import java.util.ArrayList;

public class DataManagerUtils {

    public static ArrayList<Mixinfo> getData(Class sourceClass){
        ArrayList<Mixinfo> data=null;
        if(sourceClass ==MixListAdapter.class){
            data = MixShowActivity.data;
        }
        else if(sourceClass ==CollectionListAdapter.class){
            data = MyCollectionActivity.data;
        }
        else if(sourceClass == DynamicListAdapter.class){
            data = MyDynamicActivity.data;
        }
        return data;
    }

    public static Mixinfo getMixinfo(Class sourceClass,int position){
        Mixinfo mixinfo=null;
        if(sourceClass ==MixListAdapter.class){
            mixinfo= MixShowActivity.data.get(position);
        }
        else if(sourceClass ==CollectionListAdapter.class){
            mixinfo = MyCollectionActivity.data.get(position);
        }
        else if(sourceClass == DynamicListAdapter.class){
            mixinfo = MyDynamicActivity.data.get(position);
        }
        else if(sourceClass== DetailsFromCommentActivity.class){
            mixinfo = DetailsFromCommentActivity.mixinfo;
        }
        return mixinfo;
    }

    public static CommentListData getCommentListData(Class sourceClass,int position){
        CommentListData commentListData=null;
        if(sourceClass ==MixListAdapter.class){
            commentListData= MixShowActivity.data.get(position).commentListData;
        }
        else if(sourceClass ==CollectionListAdapter.class){
            commentListData= MyCollectionActivity.data.get(position).commentListData;
        }
        else if(sourceClass == DynamicListAdapter.class){
            commentListData= MyDynamicActivity.data.get(position).commentListData;
        }
//        else if(sourceClass == MyCommentActivity.class){
//            commentListData= MyCommentActivity.data;
//        }
        else if(sourceClass== DetailsFromCommentActivity.class){
            commentListData= DetailsFromCommentActivity.mixinfo.commentListData;
        }
        return commentListData;
    }


    public static void setMixinfo(Class sourceClass, int position, Mixinfo mixinfo){
        if(sourceClass ==MixListAdapter.class){
            MixShowActivity.data.set(position,mixinfo);
        }
        else if(sourceClass ==CollectionListAdapter.class){
            MyCollectionActivity.data.set(position,mixinfo);
        }
        else if(sourceClass == DynamicListAdapter.class){
            MyDynamicActivity.data.set(position,mixinfo);
        }
    }

    public static void notifyDataSetChanged(Class sourceClass){
        if(sourceClass ==MixListAdapter.class){
            MixShowActivity.adapterData.notifyDataSetChanged();
        }
        else if(sourceClass ==CollectionListAdapter.class){
            MyCollectionActivity.adapterData.notifyDataSetChanged();
        }
        else if(sourceClass == DynamicListAdapter.class){
            MyDynamicActivity.adapterData.notifyDataSetChanged();
        }
    }

}
