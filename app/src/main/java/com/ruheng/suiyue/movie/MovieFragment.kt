package com.ruheng.suiyue.movie

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.ruheng.suiyue.R
import com.ruheng.suiyue.base.BaseFragment
import com.ruheng.suiyue.data.bean.MovieListBean
import com.ruheng.suiyue.data.bean.SubjectsItem
import kotlinx.android.synthetic.main.fragment_movie.*

/**
 * Created by lvruheng on 2018/2/28.
 */
class MovieFragment : BaseFragment(), MovieContract.View {

    lateinit var mPresenter: MoviePresenter
    var mLastRefreshTime: Long = 0
    var mOnlineAdapter: OnlineAdapter? = null
    var mComingAdapter: ComingAdapter? = null
    var mTopAdapter: TopAdapter? = null
    var mOnlineList = ArrayList<SubjectsItem>()
    var mComingList = ArrayList<SubjectsItem>()
    var mTopList = ArrayList<SubjectsItem>()
    override fun getLayoutResources(): Int {
        return R.layout.fragment_movie
    }

    override fun initView(savedInstanceState: Bundle?) {
        var onlineLayoutManager = LinearLayoutManager(context)
        onlineLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_online.layoutManager = onlineLayoutManager
        mOnlineAdapter = OnlineAdapter(context!!, mOnlineList)
        rv_online.adapter = mOnlineAdapter
        var comingLayoutManager = LinearLayoutManager(context)
        comingLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_coming.layoutManager = comingLayoutManager
        mComingAdapter = ComingAdapter(context!!, mComingList)
        rv_coming.adapter = mComingAdapter
        var topLayoutManager = LinearLayoutManager(context)
        rv_top.layoutManager = topLayoutManager
        mTopAdapter = TopAdapter(context!!, mTopList)
        rv_top.adapter = mTopAdapter
        //todo 滑动冲突
    }
    override fun setOnlineList(movieListBean: MovieListBean) {
        if (mOnlineList?.size!! > 0) {
            mOnlineList?.clear()
        }
        movieListBean.subjects?.forEach {
            //只展示6个上映电影
            if (mOnlineList.size < 6) {
                mOnlineList?.add(it)
            }
        }
        mOnlineAdapter?.notifyDataSetChanged()
    }

    override fun setComingList(movieListBean: MovieListBean) {
        if (mComingList?.size!! > 0) {
            mComingList?.clear()
        }
        movieListBean.subjects?.forEach {
            //只展示8个即将上映电影
            if (mComingList.size < 8) {
                mComingList?.add(it)
            }
        }
        mComingAdapter?.notifyDataSetChanged()
    }

    override fun setTopList(movieListBean: MovieListBean) {
        if (mTopList?.size!! > 0) {
            mTopList?.clear()
        }
        movieListBean.subjects?.forEach {
            //只展示5个Top 250电影
            if (mTopList.size < 5) {
                mTopList?.add(it)
            }
        }
        mTopAdapter?.notifyDataSetChanged()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            //距离上次刷新超过6分钟，重新加载数据
            if (System.currentTimeMillis().minus(mLastRefreshTime) > 3600000) {
                mPresenter.start()
            }
            mLastRefreshTime = System.currentTimeMillis()
        }
    }

    override fun setPresenter(presenter: MovieContract.Presenter) {
        mPresenter = presenter as MoviePresenter
    }


    override fun isActive(): Boolean {
        return isAdded
    }

    override fun getBookContext(): Context? {
        return if (isActive()) {
            context
        } else {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}