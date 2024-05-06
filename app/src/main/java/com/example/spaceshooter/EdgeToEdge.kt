import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object EdgeToEdge {
    fun enable(activity: Activity) {
        activity.window.decorView.addOnAttachStateChangeListener(object :
            View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                enableEdgeToEdge(activity)
            }

            override fun onViewDetachedFromWindow(v: View) {
                // No-op
            }
        })
    }

    private fun enableEdgeToEdge(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.let { controller ->
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsets.Type.systemBars())
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(
                activity.findViewById<View>(android.R.id.content),
                object : OnApplyWindowInsetsListener {
                    override fun onApplyWindowInsets(
                        v: View,
                        insets: WindowInsetsCompat
                    ): WindowInsetsCompat {
                        v.setPadding(
                            insets.systemWindowInsetLeft,
                            insets.systemWindowInsetTop,
                            insets.systemWindowInsetRight,
                            insets.systemWindowInsetBottom
                        )
                        return insets
                    }
                }
            )
            ViewCompat.requestApplyInsets(activity.findViewById<View>(android.R.id.content))
        }
    }

}
