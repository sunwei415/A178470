/**
  * Generated by smali2java 1.0.0.558
  * Copyright (C) 2013 Hensence.com
  */

package com.mobcent.discuz.base.fragment;

import com.mobcent.discuz.module.custom.widget.layout.CustomLayoutOutSide;
import android.widget.BaseAdapter;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.mobcent.discuz.android.model.ConfigComponentModel;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.mobcent.lowest.base.utils.MCResource;
import android.widget.FrameLayout;
import com.mobcent.lowest.base.utils.MCLogUtil;
import android.app.Activity;
import com.mobcent.lowest.base.utils.MCPhoneUtil;
import android.content.Context;
import com.mobcent.lowest.android.ui.widget.PullToRefreshListView;
import com.mobcent.discuz.application.DiscuzApplication;
import com.mobcent.discuz.android.model.BaseResultModel;
import com.mobcent.discuz.android.model.ConfigModel;
import com.mobcent.discuz.base.task.ModuleTask;
import android.support.v4.app.FragmentActivity;
import com.mobcent.discuz.android.model.ConfigModuleModel;
import com.mobcent.discuz.base.task.BaseRequestCallback;
import android.os.Bundle;
import java.util.ArrayList;
import android.widget.ListAdapter;

public class CustomPageFragment extends BaseModuleFragment {
    private CustomPageFragment.ListAdapter adapter;
    private List<ConfigComponentModel> componentModels;
    private PullToRefreshListView list;
    
    public CustomPageFragment() {
    }
    
    protected String getRootLayoutName() {
        return "base_custom_fragment";
    }
    
    protected void initDatas(Bundle savedInstanceState) {
        super.initDatas(savedInstanceState);
        componentModels = new ArrayList();
    }
    
    protected void initViews(View rootView) {
        super.initViews(rootView);
        list = (PullToRefreshListView)findViewByName(rootView, "pull_refresh_list");
        list.setAutoLoadMore(false);
        if(adapter == null) {
            adapter = new CustomPageFragment.ListAdapter(this);
        }
        list.setAdapter(adapter);
    }
    
    protected void initActions(View rootView) {
        list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener(this) {
            
            public void onRefresh() {
                if(!DiscuzApplication._instance.isPayed()) {
                    list.onRefreshComplete();
                    list.onBottomRefreshComplete(0x6);
                    return;
                }
            }
        });
    }
    
    protected void firstCreate() {
        super.firstCreate();
        if(DiscuzApplication._instance.isPayed()) {
            loadData(true);
        }
    }
    
    private void loadData(boolean isInit) {
        long configId = 0x0;
        BaseResultModel<ConfigModel> configResult = getAppApplication().getConfigModel();
        if((configResult != null) && (configResult.getData() != null) && (configResult.getRs() == 0x1)) {
            configId = (ConfigModel)configResult.getData().getConfigId();
        }
        if(isInit) {
            (getActivity(), true, moduleModel.getId(), configId, new BaseRequestCallback(this, isInit) {
                
                public void onPreExecute() {
                    list.onBottomRefreshComplete(0x6);
                }
                
                public void onPostExecute(BaseResultModel<ConfigModuleModel> result) {
                    if(getActivity() == null) {
                        return;
                    }
                    if(!isInit) {
                        list.onRefreshComplete();
                    }
                    if((result.getRs() != 0) && (result.getData() != null) && (!MCListUtils.isEmpty((ConfigModuleModel)result.getData().getComponentList()))) {
                        componentModels.clear();
                        componentModels.addAll((ConfigModuleModel)result.getData().getComponentList());
                        adapter.notifyDataSetChanged();
                        if(!isInit) {
                            list.onBottomRefreshComplete(0x6);
                        }
                    } else if(!isInit) {
                        list.onBottomRefreshComplete(0x4);
                    }
                    if(isInit) {
                        mHandler.postDelayed(new Runnable(this) {
                            
                            public void run() {
                                list.onRefresh(true);
                            }
                        }, 0x1f4);
                    }
                }
            }).execute(new Void[0x0]);
        }
        0x1 = 0x0;
    }
    
    class ListAdapter extends BaseAdapter {
        private Map<Integer, Integer> itemsHeight = new HashMap();
        
        public ListAdapter(CustomPageFragment p1) {
        }
        
        public int getCount() {
            return componentModels.size();
        }
        
        public ConfigComponentModel getItem(int position) {
            return (ConfigComponentModel)componentModels.get(position);
        }
        
        public long getItemId(int position) {
            return (long)position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomPageFragment.Holder holder = 0x0;
            if(convertView == null) {
                convertView = inflater.inflate(resource.getLayoutId("base_custom_fragment_item"), 0x0);
                CustomPageFragment.Holder holder = new CustomPageFragment.Holder(this$0);
                holder.outSideBox = (CustomLayoutOutSide)convertView.findViewById(resource.getViewId("out_side_layout"));
                convertView.setTag(holder);
            } else {
                holder = convertView.getTag();
            }
            ConfigComponentModel component = getItem(position);
            FrameLayout.LayoutParams lps = (FrameLayout.LayoutParams)holder.outSideBox.getLayoutParams();
            boolean isAnim = 0x0;
            if((itemsHeight.get(Integer.valueOf(position)) != null) && ((Integer)itemsHeight.get(Integer.valueOf(position)).intValue() != 0)) {
                MCLogUtil.e(TAG, "set item height " + itemsHeight.get(Integer.valueOf(position)));
                lps.height = (Integer)itemsHeight.get(Integer.valueOf(position)).intValue();
                isAnim = true;
            } else {
                lps.height = -0x2;
                isAnim = true;
            }
            if(position == (getCount() - 0x1)) {
                lps.bottomMargin = MCPhoneUtil.dip2px(activity, 0.0f);
            } else {
                lps.bottomMargin = 0x0;
            }
            if(position == 0) {
                lps.topMargin = 0x0;
            } else {
                lps.topMargin = MCPhoneUtil.dip2px(activity, 0.0f);
            }
            holder.outSideBox.setLayoutParams(lps);
            holder.outSideBox.initViews(component, isAnim);
            holder.outSideBox.measure(lps.width, lps.height);
            int heightMeasure = holder.outSideBox.getMeasuredHeight();
            MCLogUtil.e(TAG, "position == " + position + "====heightMeasure===" + heightMeasure);
            if((heightMeasure > 0) && (itemsHeight.get(Integer.valueOf(position)) == null) || ((Integer)itemsHeight.get(Integer.valueOf(position)).intValue() == 0)) {
                itemsHeight.put(Integer.valueOf(position), Integer.valueOf(heightMeasure));
            }
            return convertView;
        }
        
        public void notifyDataSetChanged() {
            itemsHeight.clear();
            super.notifyDataSetChanged();
        }
    }
    
    class Holder {
        public CustomLayoutOutSide outSideBox;
    }
}