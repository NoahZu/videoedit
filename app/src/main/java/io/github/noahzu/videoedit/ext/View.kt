package io.github.noahzu.videoedit.ext

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.load.resource.gif.GifDrawable

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.isInvisible() = this.visibility == View.INVISIBLE

fun View.isGone() = this.visibility == View.GONE

fun View.beVisible() {
    this.visibility = View.VISIBLE
}

fun View.beGone() {
    this.visibility = View.GONE
}

fun View.beInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.beVisibleIf(beVisible: Boolean) {
    if (beVisible) beVisible() else beGone()
}

fun View.beInvisibleIf(beInvisible: Boolean) {
    if (beInvisible) beInvisible() else beVisible()
}

fun View.beGoneIf(beGone: Boolean) {
    if (beGone) beGone() else beVisible()
}

fun View.setNoDoubleClickCallback(callback: ((View) -> Unit)?) {
    val listener =
        if (callback == null) null else object : NoDoubleClickListener(NoDoubleClickListener.MINI_CLICK_SCOPE) {
            override fun onNoDoubleClick(v: View) {
                callback.invoke(v)
            }
        }
    setOnClickListener(listener)
}

fun View.setNoDoubleClickCallback(callback: ((View) -> Unit)?, clickScope: Long) {
    val listener = if (callback == null) null else object : NoDoubleClickListener(clickScope) {
        override fun onNoDoubleClick(v: View) {
            callback.invoke(v)
        }
    }
    setOnClickListener(listener)
}

abstract class NoDoubleClickListener(val clickScope: Long) : View.OnClickListener {
    companion object {
        const val MINI_CLICK_SCOPE = 500L
    }

    private var lastClickTime = 0L

    final override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime > clickScope) {
            lastClickTime = curClickTime
            onNoDoubleClick(v)
        }
    }

    abstract fun onNoDoubleClick(v: View)
}

fun View.setMargin(left: Int, top: Int, right: Int, bottom: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.leftMargin = left
        lp.rightMargin = right
        lp.topMargin = top
        lp.bottomMargin = bottom
        layoutParams = lp
    }
}

fun View.setMarginLeft(left: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.leftMargin = left
        layoutParams = lp
    }
}

fun View.setMarginBottom(bottom: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.bottomMargin = bottom
        layoutParams = lp
    }
}

fun View.setMarginTop(top: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.topMargin = top
        layoutParams = lp
    }
}


fun View.setViewMarginRight(right: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.rightMargin = right
        layoutParams = lp
    }
}

fun View.setMarginRight(right: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.rightMargin = right
        layoutParams = lp
    }
}

fun View.setLRPadding(left: Int, right: Int) {
    setPadding(left, paddingTop, right, paddingBottom)
}

fun View.setTBPadding(top: Int, bottom: Int) {
    setPadding(paddingLeft, top, paddingRight, bottom)
}

fun View.setTopPadding(top: Int) {
    setTBPadding(top, paddingBottom)
}

fun View.setBottomPadding(bottom: Int) {
    setTBPadding(paddingTop, bottom)
}

fun View.setRightPadding(right: Int) {
    setLRPadding(paddingLeft, right)
}

fun View.setLeftPadding(left: Int) {
    setLRPadding(left, paddingRight)
}

fun View.adjustSize(width: Int, height: Int) {
    val lp = this.layoutParams
    if (lp.width != width || lp.height != height) {
        lp.width = width
        lp.height = height
        this.layoutParams = lp
    }
}

fun View.adjustHeight(height: Int) {
    val lp = layoutParams
    lp.height = height
    layoutParams = lp
}

fun View.adjustWidth(width: Int) {
    val lp = layoutParams
    lp.width = width
    layoutParams = lp
}

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    this.setTextColor(ContextCompat.getColor(context, colorRes))
}

fun TextView.setTextSizeDp(@DimenRes dimenRes: Int) {
    this.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(dimenRes))
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

fun View.onGlobalLayout(callback: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

fun EditText.afterTextChanged(callback: (s: Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            callback(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    })
}

fun EditText.setOnTextChanged(callback: () -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(string: CharSequence?, p1: Int, p2: Int, p3: Int) {
            callback()
        }
    })
}

fun Animation.onAnimationEnd(callback: (animation: Animation?) -> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            callback.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
        }
    })
}

fun ObjectAnimator.onAnimationEnd(callback: (animation: Animator?) -> Unit) {
    this.addListener(object : Animator.AnimatorListener {

        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            callback.invoke(animation)
        }

        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }
    })
}

fun AnimatorSet.onAnimatorSetEnd(callback: ((animation: Animator?) -> Unit)?) {
    this.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            callback?.invoke(animation)
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    })
}

fun ViewPager.onPageSelected(callback: (position: Int) -> Unit) {

    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        }

        override fun onPageSelected(p0: Int) {
            callback.invoke(p0)
        }
    })
}

fun ImageView.applyColorFilter(color: Int) = setColorFilter(color, PorterDuff.Mode.SRC_IN)

fun ImageView.startAnim() {
    if (drawable is AnimationDrawable)
        (drawable as AnimationDrawable).start()
}

fun ImageView.stopAnim() {
    if (drawable is AnimationDrawable)
        (drawable as AnimationDrawable).stop()
}


fun View.adjustSizeByWidth(srcWidth: Int, srcHeight: Int, destWidth: Int, defRatio: Float) {
    if (destWidth > 0) {
        if (srcWidth > 0 && srcHeight > 0) {
            layoutParams.width = destWidth
            layoutParams.height = (destWidth / (1.000F * srcWidth / srcHeight)).toInt()
            layoutParams = layoutParams
        } else if (defRatio > 0F) {
            layoutParams.width = destWidth
            layoutParams.height = (destWidth / defRatio).toInt()
            layoutParams = layoutParams
        }
    }
}


fun EditText.setRequestFocus(){
    this.isFocusable=true
    this.isFocusableInTouchMode=true
    this.requestFocus()
}

fun EditText.showSoftInput() {
    if (this.context == null) {
        return
    }
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    this.postDelayed({ imm.showSoftInput(this, InputMethodManager.SHOW_FORCED) }, 200)
}

fun EditText.hideSoftInput() {
    if (this.context == null) {
        return
    }
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun TextView.clear() {
    this.text = ""
}

fun View.toBitmap(mWidth: Int=0,mHeight: Int=0): Bitmap? {
    var viewBitmap:Bitmap?=null
    if(mWidth>0 && mWidth>0){
        viewBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        this.measure(mWidth, mHeight)
        this.layout(0, 0, mWidth, mHeight)
    }else{
        if(this.width==0 || this.height==0){
            return null
        }else{
            viewBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
            this.layout(0, 0, this.width, this.height)
        }
    }
    if(viewBitmap==null) return null

    val c = Canvas(viewBitmap)
    this.draw(c)
    return viewBitmap
}
