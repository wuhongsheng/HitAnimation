package com.hd.hitanimation

import android.content.Context
import android.util.Log
import android.view.View
import com.wt.hitanimation.HitAnimation


/**
 * description
 * @author whs
 * @date 2022/1/30
 */

class HitAnimationHelper private constructor(context: Context){
    private var hitAnimation: HitAnimation? = null
    private val TAG: String = HitAnimationHelper::class.java.getSimpleName()

    companion object {
        @Volatile private var instance: HitAnimationHelper? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: HitAnimationHelper(context).also { instance = it }
            }
    }

    init {
        Log.i(TAG, "init")
        hitAnimation =  HitAnimation.Builder(context)
            .setCallBackListener {
                mAnchor -> mAnchor.performClick()
        }.build()
    }


    fun startAnimation(v: View?) {
        Log.i(TAG, "startAnimation")
        if (hitAnimation!!.isVisibleToUser(v))
        {
            hitAnimation?.handleEvent(v)
        } else {
            Log.i(TAG, "hit view is not visible to user")
        }
    }

    fun release(){
        hitAnimation?.release()
    }

}