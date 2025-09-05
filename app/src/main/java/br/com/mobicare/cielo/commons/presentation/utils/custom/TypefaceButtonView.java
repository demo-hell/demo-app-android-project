package br.com.mobicare.cielo.commons.presentation.utils.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.util.HashMap;
import java.util.Map;

import br.com.mobicare.cielo.R;

/**
 * Created by benhur.souza on 11/05/2017.
 */

public class TypefaceButtonView extends AppCompatButton {
    public static Map<String, Typeface> typefaceCache = new HashMap();

    public TypefaceButtonView(Context context) {
        super(context);
    }

    public TypefaceButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setTypeface(attrs, this);
        }
    }

    public TypefaceButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setTypeface(attrs, this);
        }
    }

    private void setTypeface(AttributeSet attrs, TextView textView) {
        Context context = textView.getContext();
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomTypeFace);
        String typefaceName = values.getString(R.styleable.CustomTypeFace_typeface);
        values.recycle();
        if(typefaceCache.containsKey(typefaceName)) {
            setType(textView, (Typeface)typefaceCache.get(typefaceName));
        } else {
            Typeface typeface;
            try {
                typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + typefaceName);
            } catch (Exception var7) {
                return;
            }

            typefaceCache.put(typefaceName, typeface);
            setType(textView, typeface);

        }


    }

    private void setType(TextView textView, Typeface typeface){
        if( this.getTypeface() == null){
            textView.setTypeface(typeface);
        }else{
            textView.setTypeface(typeface, this.getTypeface().getStyle());
        }

    }
}
