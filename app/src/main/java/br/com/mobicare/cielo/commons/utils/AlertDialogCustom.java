package br.com.mobicare.cielo.commons.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import br.com.mobicare.cielo.R;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Benhur on 12/05/17.
 */

public class AlertDialogCustom {
    private final String screenTitle;
    private final String title;
    private final String message;
    private final String btnRightLabel;
    private final String btnLeftLabel;
    private final View view;
    private final boolean isCancelable;
    private boolean taguear = true;
    @LayoutRes private final Integer customLayout;
    private final View.OnClickListener listenerRight;
    private final View.OnClickListener listenerLeft;

    public static class Builder {
        private String screenTitle = null;
        private String title = null;
        private String message = null;
        private String btnRight = null;
        private String btnLeft = null;
        private View view = null;
        private boolean taguear = true;
        private View.OnClickListener listenerRight;
        private View.OnClickListener listenerLeft;
        private final Context context;
        private boolean isCancelable = true;
        private @LayoutRes Integer customLayout;

//        public Builder(Context context) {
//            this.context = context;
//        }

        public Builder(Context context, String screenTitle) {
            this.context = context;
            this.screenTitle = screenTitle;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(@StringRes int titleId) {
//            if(title != null && !title.isEmpty()) {
//                this.title = title;
//            }else
            if (titleId != -1) {
                this.title = context.getString(titleId);
            }
            return this;
        }

        public Builder setMessage(String message, @StringRes int stringId) {
            if (message != null && !message.isEmpty()) {
                this.message = message;
            } else if (stringId != -1) {
                this.message = context.getString(stringId);
            }
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public Builder setBtnRight(String btnRight) {
            this.btnRight = btnRight;
            return this;
        }

        public Builder setBtnLeft(String btnLeft) {
            this.btnLeft = btnLeft;
            return this;
        }

        public Builder setOnclickListenerRight(View.OnClickListener listener) {
            this.listenerRight = listener;
            return this;
        }

        public void performClickRight() {
            if (this.listenerRight != null) {
                this.listenerRight.onClick(null);
            }
        }

        public Builder setTaguear(boolean tagueamento) {
            this.taguear = tagueamento;
            return this;
        }

        public Builder setOnclickListenerLeft(View.OnClickListener listener) {
            this.listenerLeft = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.isCancelable = cancelable;
            return this;
        }

        /**
         * Apresenta a mensagem retornando o controle do dialog, para futura dismiss do mesmo.
         * O retorno do dialog é mecessário para fechar o mesmo de onDestroyView
         * Erro do crashlytics
         *
         * @return
         */
        public Dialog show() {
            return (new AlertDialogCustom(this)).show(context);
        }

        public Integer getCustomLayout() {
            return customLayout;
        }

        public void setCustomLayout(@LayoutRes Integer customLayout) {
            this.customLayout = customLayout;
        }
    }

    public AlertDialogCustom(Builder builder) {
        this.screenTitle = builder.screenTitle;
        this.title = builder.title;
        this.message = builder.message;
        this.btnRightLabel = builder.btnRight;
        this.btnLeftLabel = builder.btnLeft;
        this.view = builder.view;
        this.listenerLeft = builder.listenerLeft;
        this.listenerRight = builder.listenerRight;
        this.taguear = builder.taguear;
        this.isCancelable = builder.isCancelable;
        this.customLayout = builder.customLayout;
    }


    public Dialog show(final Context context) {
        if (context == null) {
            return null;
        }

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (customLayout != null) {
            dialog.setContentView(customLayout);
        } else {
            dialog.setContentView(R.layout.custom_dialog);
        }

        dialog.setTitle("");
        dialog.setCancelable(isCancelable);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.custom_dialog_title);
        TextView txtMessage = (TextView) dialog.findViewById(R.id.custom_dialog_message);

        configureContainerView(dialog);

        View firstButton = dialog.findViewById(R.id.custom_dialog_button_right);
        View secondButton = dialog.findViewById(R.id.custom_dialog_button_left);

        if (title == null) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
        }

        if (message == null) {
            txtMessage.setVisibility(View.GONE);
        } else {
            txtMessage.setText(SpannableStringBuilder.valueOf(message));
        }


        if (firstButton instanceof FancyButton &&
                secondButton instanceof FancyButton) {
            configureFancyButtons((Activity) context, dialog,
                    (FancyButton) firstButton,
                    (FancyButton) secondButton);
        } else {
            assert firstButton instanceof Button;
            configureButtons((Activity) context, dialog,
                    (Button) firstButton,
                    (Button) secondButton);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());


        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dialog.show();
        dialog.getWindow().setAttributes(params);

        if (customLayout != null) {
            dialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (taguear) {
            if (message == null) {
                sendGA(context, screenTitle, title);
            } else {
                sendGA(context, screenTitle, message);
            }
        }

        return dialog;
    }

    private void configureContainerView(Dialog dialog) {
        if (customLayout == null) {
            ViewGroup viewContainer = (ViewGroup) dialog.findViewById(R.id.container_view);

            if (view == null) {
                viewContainer.setVisibility(View.GONE);
            } else {
                viewContainer.addView(view);
            }
        }
    }

    private void configureFancyButtons(Activity context, Dialog dialog,
                                       FancyButton firstButton,
                                       FancyButton secondButton) {

        configureFancyButtonLabels(firstButton, secondButton);
        configureFancyButtonListeners(context, dialog, firstButton, secondButton);
    }

    private void configureFancyButtonLabels(FancyButton btnRight, FancyButton btnLeft) {
        if (btnLeftLabel == null) {
            btnLeft.setVisibility(View.GONE);
        } else {
            btnLeft.setText(btnLeftLabel);
        }

        if (btnRightLabel == null) {
            btnRight.setVisibility(View.GONE);
        } else {
            btnRight.setText(btnRightLabel);
        }
    }

    private void configureFancyButtonListeners(Activity context,
                                          Dialog dialog,
                                          FancyButton btnRight,
                                          FancyButton btnLeft) {

        btnLeft.setOnClickListener(view -> {
            dialog.dismiss();
            if (listenerLeft != null) {
                listenerLeft.onClick(view);
            }
        });


        btnRight.setOnClickListener(view -> {
            dialog.dismiss();
            if (!context.isFinishing()) {
                if (listenerRight != null && view != null) {
                    try {
                        listenerRight.onClick(view);
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }
            }

        });
    }



    private void configureButtons(Activity context, Dialog dialog,
                                  Button firstButton,
                                  Button secondButton) {

        configureButtonLabels(firstButton, secondButton);
        configureButtonListeners(context, dialog, firstButton, secondButton);
    }

    private void configureButtonLabels(Button btnRight, Button btnLeft) {
        if (btnLeftLabel == null) {
            btnLeft.setVisibility(View.GONE);
        } else {
            btnLeft.setText(btnLeftLabel);
        }

        if (btnRightLabel == null) {
            btnRight.setVisibility(View.GONE);
        } else {
            btnRight.setText(btnRightLabel);
        }
    }

    private void configureButtonListeners(Activity context,
                                          Dialog dialog,
                                          Button btnRight,
                                          Button btnLeft) {
        btnLeft.setOnClickListener(view -> {
            dialog.dismiss();
            if (listenerLeft != null) {
                listenerLeft.onClick(view);
            }
        });


        btnRight.setOnClickListener(view -> {
            dialog.dismiss();
            if (!context.isFinishing()) {
                if (listenerRight != null && view != null) {
                    try {
                        listenerRight.onClick(view);
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }
            }

        });
    }

    private void sendGA(Context context, String screenTitle, String label) {
    }
}
