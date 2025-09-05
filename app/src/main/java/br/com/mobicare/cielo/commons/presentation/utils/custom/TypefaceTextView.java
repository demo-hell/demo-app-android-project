package br.com.mobicare.cielo.commons.presentation.utils.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.HashMap;
import java.util.Map;

import br.com.mobicare.cielo.R;

/**
 * Created by benhur.souza on 19/04/2017.
 */

public class TypefaceTextView extends AppCompatTextView {


    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static Map<String, Typeface> typefaceCache = new HashMap();


    public TypefaceTextView(Context context) {
        super(context);
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            setTypeface(attrs, this);
        }
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            setTypeface(attrs, this);
        }

    }


    private void setTypeface(AttributeSet attrs, TextView textView) {
        Context context = textView.getContext();
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomTypeFace);
        String typefaceName = values.getString(R.styleable.CustomTypeFace_typeface);

        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle",
                Typeface.NORMAL);

        values.recycle();
        if(typefaceCache.containsKey(typefaceName)) {
            setType(textView, (Typeface)typefaceCache.get(typefaceName), textStyle);
        } else {
            Typeface typeface;
            try {
                typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + typefaceName);
            } catch (Exception var7) {
                return;
            }

            typefaceCache.put(typefaceName, typeface);
            setType(textView, typeface, textStyle);

        }


    }

    private void setType(TextView textView, Typeface typeface, int textStyle) {
        if (this.getTypeface() == null) {
            textView.setTypeface(typeface);
        } else {
            textView.setTypeface(typeface, textStyle);
        }
    }


}
