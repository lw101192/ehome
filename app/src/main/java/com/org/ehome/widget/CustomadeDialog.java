package com.org.ehome.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.org.ehome.R;


public class CustomadeDialog extends Dialog {

    public CustomadeDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomadeDialog(Context context, boolean cancelable,
                           OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public CustomadeDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }


    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private boolean mCancelable;
        private boolean mAutoDismissenable = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 从资源文件获取对话框对话框message并设置
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * 从资源文件获取对话框对话框title并设置
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * 设置对话框对话框title
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * 设置确认按钮文字和监听器
         *
         * @param positiveButtonText
         * @return
         */


        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setAutoDismiss(boolean AutoDismissenable) {
            mAutoDismissenable = AutoDismissenable;
            return this;
        }

        public CustomadeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 实例化
            final CustomadeDialog dialog = new CustomadeDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.customadeview_dialog, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

//            dialog.setCancelable(mCancelable);
//            if (mCancelable) {
//                dialog.setCanceledOnTouchOutside(true);
//            }
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            if (positiveButtonText == null && negativeButtonText == null) {
                ((LinearLayout) layout.findViewById(R.id.option)).setVisibility(View.GONE);
            } else {
                if (positiveButtonText != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setText(positiveButtonText);
                    if (positiveButtonClickListener != null) {
                        ((Button) layout.findViewById(R.id.positiveButton))
                                .setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        positiveButtonClickListener.onClick(dialog,
                                                DialogInterface.BUTTON_POSITIVE);
                                        if (mAutoDismissenable)
                                            dialog.dismiss();
                                    }
                                });
                    } else {
                        ((Button) layout.findViewById(R.id.positiveButton))
                                .setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        if (mAutoDismissenable)
                                            dialog.dismiss();
                                    }
                                });
                    }
                } else {
                    // 如果没有定义positive按钮，则将确认按钮View.GONE
                    layout.findViewById(R.id.positiveButton).setVisibility(
                            View.GONE);
                }
                if (negativeButtonText != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setText(negativeButtonText);
                    if (negativeButtonClickListener != null) {
                        ((Button) layout.findViewById(R.id.negativeButton))
                                .setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        negativeButtonClickListener.onClick(dialog,
                                                DialogInterface.BUTTON_NEGATIVE);
                                        if (mAutoDismissenable)
                                            dialog.dismiss();
                                    }
                                });
                    } else {
                        ((Button) layout.findViewById(R.id.negativeButton))
                                .setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        if (mAutoDismissenable)
                                            dialog.dismiss();
                                    }
                                });
                    }
                } else {
                    // 如果没有定义negative按钮，则将确认按钮View.GONE
                    layout.findViewById(R.id.negativeButton).setVisibility(
                            View.GONE);
                }
            }

            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

            } else if (contentView == null) {
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
            }
            dialog.setContentView(layout);
            return dialog;
        }


    }

}
