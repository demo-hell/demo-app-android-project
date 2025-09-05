package br.com.mobicare.cielo.commons.presentation.utils.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.com.mobicare.cielo.R;
import br.com.mobicare.cielo.commons.utils.Utils;
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.payments.PaymentStep2Fragment;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Benhur on 12/05/17.
 */

public class TypefaceEditTextView extends AppCompatEditText {

    public static Map<String, Typeface> typefaceCache = new HashMap();

    private boolean disableCopyToClipboard = false;

    public void setDisableCopyToClipboard(boolean disableCopyToClipboard) {
        this.disableCopyToClipboard = disableCopyToClipboard;
        if (this.disableCopyToClipboard) {
            this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }
    }

    public TypefaceEditTextView(Context context) {
        super(context);
    }

    public TypefaceEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setCustomType(attrs, this);
        }
    }

    public TypefaceEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setCustomType(attrs, this);
        }
    }

    private void setCustomType(AttributeSet attrs, TextView textView) {
        Context context = textView.getContext();
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomTypeFace);

        setTypeface(values, this);

        String mask = values.getString(R.styleable.CustomTypeFace_mask);
        if (mask != null && !mask.isEmpty()) {
            this.addTextChangedListener(getMask(mask, this));
        }

        values.recycle();
    }

    private void setTypeface(TypedArray values, TextView textView) {
        String typefaceName = values.getString(R.styleable.CustomTypeFace_typeface);
        this.disableCopyToClipboard = values.getBoolean(R.styleable.CustomTypeFace_disableCopyToClipboard, false);

        if (typefaceCache.containsKey(typefaceName)) {
            setType(textView, (Typeface) typefaceCache.get(typefaceName));
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
        setDisableCopyToClipboard(this.disableCopyToClipboard);
    }

    private void setType(TextView textView, Typeface typeface) {
        if (this.getTypeface() == null) {
            textView.setTypeface(typeface);
        } else {
            textView.setTypeface(typeface, this.getTypeface().getStyle());
        }

    }

    public TextWatcher getMask(final String mask, final EditText editText) {

        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String str = Utils.INSTANCE.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //beforeTextChanged
            }

            public void afterTextChanged(Editable s) {
                //afterTextChanged
            }
        };
    }

    private static final String maskCNPJ = "##.###.###/####-##";
    private static final String maskCPF = "###.###.###-##";

    private static final String maskConta = "###########-# ###########-# ###########-# ###########-#";
    private static final String maskBoleto = "#####.##### #####.###### #####.###### # ##############";


    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }


    /**
     * method to insert mask in the Edittext if is cpf or cnpj
     *
     * @param cpforcnpj
     * @return TextWatcher
     */
    public TextWatcher validateCpforCnpj(final EditText cpforcnpj) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = TypefaceEditTextView.unmask(s.toString());
                String mask;
                String defaultMask = getDefaultMask(str);
                switch (str.length()) {
                    case 11:
                        mask = maskCPF;
                        break;
                    case 14:
                        mask = maskCNPJ;
                        break;

                    default:
                        mask = defaultMask;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                cpforcnpj.setText(mascara);
                cpforcnpj.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }


    public TextWatcher validateCpf(final EditText cpforcnpj) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = TypefaceEditTextView.unmask(s.toString());
                String mask = maskCPF;

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                cpforcnpj.setText(mascara);
                cpforcnpj.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }


    private static String getDefaultMask(String str) {
        String defaultMask = maskCPF;
        if (str.length() > 11) {
            defaultMask = maskCNPJ;
        }
        return defaultMask;
    }

    // insert mask

    /**
     * method to insert mask in the Edittext if is Collection or Ticket
     *
     * @param boletoOrConta, btnCamera, {@link PaymentStep2Fragment}
     * @return TextWatcher
     */
    public TextWatcher validateBoletoOrConta(final EditText boletoOrConta, final LinearLayout btnCamera, final PaymentStep2Fragment frag) {

        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = TypefaceEditTextView.unmask(s.toString());
                String mask = null;

                int height = frag.getContext().getResources().getDisplayMetrics().heightPixels;

                if (height == 960) {
                    boletoOrConta.setPadding(0, 0, 240, 20);
                } else if (height == 1184) {
                    boletoOrConta.setPadding(0, 0, 380, 30);
                } else if (height == 1794) {
                    boletoOrConta.setPadding(0, 0, 570, 40);
                } else if (height == 2392) {
                    boletoOrConta.setPadding(0, 0, 740, 50);
                }

                if (str != null && !str.isEmpty()) {
                    Character fisrtLetter = str.charAt(0);

                    if (fisrtLetter.toString().equals("8")) {
                        mask = maskConta;
                        calculoImagCameraConta(s, btnCamera);
                    } else {
                        mask = maskBoleto;
                        calculoImagCamera(s, btnCamera);
                    }

                    String mascara = "";
                    if (isUpdating) {
                        old = str;
                        isUpdating = false;
                        return;
                    }
                    int i = 0;
                    for (char m : mask.toCharArray()) {
                        if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                            mascara += m;
                            continue;
                        }

                        try {
                            mascara += str.charAt(i);
                        } catch (Exception e) {
                            break;
                        }
                        i++;
                    }
                    isUpdating = true;
                    boletoOrConta.setText(mascara);
                    boletoOrConta.setSelection(mascara.length());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void calculoImagCamera(CharSequence s, LinearLayout btnCamera) {

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        if (s.length() >= 16 && s.length() <= 27) {
            lp.setMargins(0, 20, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);

        } else if (s.length() >= 28 && s.length() <= 41) {
            lp.setMargins(0, 50, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);
        } else if (s.length() >= 42) {
            lp.setMargins(0, 70, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);
        } else {
            lp.setMargins(0, 0, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);
        }
    }

    private void calculoImagCameraConta(CharSequence s, LinearLayout btnCamera) {

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        if (s.length() >= 15 && s.length() <= 29) {
            lp.setMargins(0, 20, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);

        } else if (s.length() >= 30 && s.length() <= 43) {
            lp.setMargins(0, 50, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);
        } else if (s.length() >= 44) {
            lp.setMargins(0, 70, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);
        } else {
            lp.setMargins(0, 0, 0, 0);
            lp.gravity = Gravity.RIGHT;
            btnCamera.setLayoutParams(lp);
        }
    }


    private String current = "";

    /**
     * method to insert mask in the Edittext
     *
     * @param value
     * @return TextWatcher
     */
    public TextWatcher getMaskMoney(final EditText value) {

        return new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (!TextUtils.isEmpty(s) && !s.toString().equals(current)) {
                    Locale myLocale = new Locale("pt", "BR");
                    value.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[R$ ,.]", "");

                    NumberFormat nf = NumberFormat.getInstance();
                    double parsed = 0;
                    try {
                        parsed = nf.parse(cleanString).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String formatted = NumberFormat.getCurrencyInstance(myLocale).format((parsed / 100));
                    current = formatted;
                    value.setText(formatted);
                    value.setSelection(formatted.length());

                    value.addTextChangedListener(this);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //beforeTextChanged
            }

            public void afterTextChanged(Editable s) {
                //afterTextChanged
            }
        };
    }

    /**
     * method to insert mask in the Edittext
     *
     * @param value
     * @return TextWatcher
     */
    public TextWatcher getMaskMoney2(final EditText value, final Button bntCancel) {

        return new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (!TextUtils.isEmpty(s) && !s.toString().equals(current)) {
                    Locale myLocale = new Locale("pt", "BR");
                    value.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[R$ ,.]", "");

                    NumberFormat nf = NumberFormat.getInstance();
                    double parsed = 0;
                    try {
                        parsed = nf.parse(cleanString).doubleValue();
                        if (parsed == 0.0) {
                            bntCancel.setEnabled(false);
                            bntCancel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_cancel_unselector));
                        } else {
                            bntCancel.setEnabled(true);
                            bntCancel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_cancel_selector));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String formatted = NumberFormat.getCurrencyInstance(myLocale).format((parsed / 100));
                    current = formatted;
                    value.setText(formatted);
                    value.setSelection(formatted.length());

                    value.addTextChangedListener(this);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //beforeTextChanged
            }

            public void afterTextChanged(Editable s) {
                //afterTextChanged
            }
        };
    }


    public TextWatcher validateFieldStep3(final TextInputLayout conteinerValue, final EditText value,
                                          final EditText name, final FancyButton button, final String textError) {

        return new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean isValueBad = isValueBadLessThan10(value, conteinerValue, textError);

                if (isValueBad || (TextUtils.isEmpty(name.getText().toString())))
                    button.setEnabled(false);
                else
                    button.setEnabled(true);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //beforeTextChanged
            }

            public void afterTextChanged(Editable s) {
                //afterTextChanged
            }
        };
    }

    private boolean isValueBadLessThan10(EditText value, TextInputLayout conteinerValue, String textError) {
        boolean isValueBad;
        String cleanString = value.getText().toString().replaceAll("[R$ ,.]", "");

        NumberFormat nf = NumberFormat.getInstance();
        try {
            double parsed = nf.parse(cleanString).doubleValue();
            if (parsed < 1000) {
                conteinerValue.setErrorEnabled(true);
                conteinerValue.setError(textError);
                isValueBad = true;
            } else {
                conteinerValue.setError(null);
                conteinerValue.setErrorEnabled(false);
                isValueBad = false;
            }
        } catch (ParseException e) {
            conteinerValue.setError(null);
            conteinerValue.setErrorEnabled(false);
            isValueBad = true;
            e.printStackTrace();
        }
        return isValueBad;
    }

    public TextWatcher validateBottonPayment(final EditText cvv, final EditText dt, final FancyButton button) {

        return new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (cvv.getText().length() > 2 && dt.getText().length() > 4)
                    button.setEnabled(true);
                else
                    button.setEnabled(false);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //beforeTextChanged
            }

            public void afterTextChanged(Editable s) {
                //afterTextChanged
            }
        };
    }
}
