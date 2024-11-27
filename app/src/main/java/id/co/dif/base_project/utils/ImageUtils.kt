package id.co.dif.base_project.utils

import android.graphics.drawable.Drawable
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import id.co.dif.base_project.R
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.CropSquareTransformation
import java.io.IOException

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 10:39
 *
 */
fun ImageView.loadImage(imageUrl: String?) {
    Picasso
        .get()
        .load(imageUrl)
        .into(this, object : Callback {
            override fun onSuccess() {

            }

            override fun onError(e: Exception?) {
                this@loadImage.setImageResource(R.drawable.ic_person)
            }

        })
}

fun ImageView.loadImage(imageUrl: String?, @DrawableRes imagePlaceholder: Int) {
    Picasso.get().load(imageUrl)
        .placeholder(imagePlaceholder)
//        .error(imagePlaceholder)
        .into(this)
}

fun gravatar(key: Any, size: Int = 200): String {
    val hash = md5(key)
    return "https://www.gravatar.com/avatar/${hash}?d=identicon&s=$size"
}

fun ImageView.loadImage(
    imageUrl: String?,
    imagePlaceholder: Drawable,
    default: Int = R.drawable.baseline_broken_image_24,
    circleCrop: Boolean = false,
    centerCrop: Boolean = false,
    skipMemoryCaching: Boolean = false
) {
    val builder = Picasso
        .get()
        .load(imageUrl)
        .placeholder(imagePlaceholder)
        .error(R.drawable.ic_person)
    if (skipMemoryCaching) {
        builder
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
    }
    if (circleCrop) {
        builder.transform(CropCircleTransformation())
    }
    if (centerCrop) {
        builder.fit().centerCrop()
    }
    builder.into(this, object : Callback {
        override fun onSuccess() {
        }

        override fun onError(e: Exception?) {
            this@loadImage.setImageResource(default)
        }

    })
}

fun picassoLoadInto(requestCreator: RequestCreator, imageView: ImageView, callback: Callback?) {
    val mainThread = Looper.myLooper() == Looper.getMainLooper()
    if (mainThread) {
        requestCreator.into(imageView, callback)
    } else {
        try {
            val bitmap = requestCreator.get()
            imageView.setImageBitmap(bitmap)
            callback?.onSuccess()
        } catch (e: IOException) {
            callback?.onError(e)
        }
    }
}

fun ImageView.loadImage(
    imageRes: Int,
    circleCrop: Boolean = false
) {
    val builder = Picasso
        .get()
        .load(imageRes)
        .error(R.drawable.ic_person)
    if (circleCrop) {
        builder.transform(CropCircleTransformation())
    }
    builder.into(this, object : Callback {
        override fun onSuccess() {}
        override fun onError(e: Exception?) {
            Log.d("TAG", "onError: ${e!!.localizedMessage}")
            this@loadImage.setImageResource(R.drawable.baseline_broken_image_24)
        }

    })
}
