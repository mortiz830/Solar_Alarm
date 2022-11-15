package com.example.solar_alarm.AlarmList

import androidx.recyclerview.widget.RecyclerView
import android.view.View.OnLongClickListener
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import android.view.View

class ItemClickSupport private constructor(private val mRecyclerView: RecyclerView?, private val mItemID: Int) {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    private val mOnClickListener = View.OnClickListener { v ->
        if (mOnItemClickListener != null) {
            val holder = mRecyclerView!!.getChildViewHolder(v)
            mOnItemClickListener!!.onItemClicked(mRecyclerView, holder.adapterPosition, v)
        }
    }
    private val mOnLongClickListener = OnLongClickListener { v ->
        if (mOnItemLongClickListener != null) {
            val holder = mRecyclerView!!.getChildViewHolder(v)
            return@OnLongClickListener mOnItemLongClickListener!!.onItemLongClicked(mRecyclerView, holder.adapterPosition, v)
        }
        false
    }
    private val mAttachListener: OnChildAttachStateChangeListener = object : OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener)
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {}
    }

    init {
        mRecyclerView!!.setTag(mItemID, this)
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?): ItemClickSupport {
        mOnItemClickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?): ItemClickSupport {
        mOnItemLongClickListener = listener
        return this
    }

    private fun detach(view: RecyclerView) {
        view.removeOnChildAttachStateChangeListener(mAttachListener)
        view.setTag(mItemID, null)
    }

    interface OnItemClickListener {
        fun onItemClicked(recyclerView: RecyclerView?, position: Int, v: View?)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(recyclerView: RecyclerView?, position: Int, v: View?): Boolean
    }

    companion object {
        fun addTo(view: RecyclerView?, itemID: Int): ItemClickSupport {
            var support = view!!.getTag(itemID) as ItemClickSupport
            if (support == null) {
                support = ItemClickSupport(view, itemID)
            }
            return support
        }

        fun removeFrom(view: RecyclerView, itemID: Int): ItemClickSupport? {
            val support = view.getTag(itemID) as ItemClickSupport
            support?.detach(view)
            return support
        }
    }
}