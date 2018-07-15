package theawesomebox.com.app.awesomebox.common.utils;

import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class PhoneNumberFormatter implements TextWatcher {
    private boolean mFormatting; // this is a flag which prevents the
    private boolean clearFlag;
    private WeakReference<EditText> mWeakEditText;
    private OnPhoneNumberTextWatcher onPhoneNumberTextWatcher = null;
    private EditText editText;


    public PhoneNumberFormatter(WeakReference<EditText> weakEditText) {
        this.mWeakEditText = weakEditText;
    }

    public PhoneNumberFormatter(AppCompatEditText etPhone) {
        this.editText = etPhone;
    }

    public static String extractDigitsFromString(Editable text) {
        // Remove everything except digits
        int p = 0;
        while (p < text.length()) {
            char ch = text.charAt(p);
            if (!Character.isDigit(ch)) {
                text.delete(p, p + 1);
            } else {
                p++;
            }
        }
        String phoneNo = text.toString();
        return AppUtils.ifNotNullEmpty(phoneNo) ? ("+" + phoneNo) : "";
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (after == 0 && s.toString().equals("92")) {
            clearFlag = true;
        }
        if (onPhoneNumberTextWatcher != null)
            onPhoneNumberTextWatcher.beforePhoneNumberTextChange(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Make sure to ignore calls to afterTextChanged caused by the work
        // done below
        if (!mFormatting) {
            mFormatting = true;
            EditText etPhoneNo = null;
            final String formattedValue = formatUsNumber(s);
            if (mWeakEditText != null)
                etPhoneNo = mWeakEditText.get();
            else etPhoneNo = editText;
            if (AppUtils.ifNotNullEmpty(formattedValue)) {
                etPhoneNo.setText(formattedValue);
                etPhoneNo.setSelection(etPhoneNo.length());
            }
            mFormatting = false;
        }
        if (onPhoneNumberTextWatcher != null)
            onPhoneNumberTextWatcher.afterPhoneNumberTextChange(s);
    }

    private String formatUsNumber(Editable text) {
        StringBuilder formattedString = new StringBuilder();
        // Remove everything except digits
        int p = 0;
        while (p < text.length()) {
            char ch = text.charAt(p);
            if (!Character.isDigit(ch)) {
                text.delete(p, p + 1);
            } else {
                p++;
            }
        }
        // Now only digits are remaining
        String allDigitString = text.toString();

        int totalDigitCount = allDigitString.length();

        if (totalDigitCount == 0
                || (totalDigitCount > 10 && !allDigitString.startsWith("92"))
                || totalDigitCount > 12) {
            // May be the total length of input length is greater than the
            // expected value so we'll remove all formatting
            text.clear();
            text.append(allDigitString);
            return allDigitString;
        }
        int alreadyPlacedDigitCount = 0;
        // Only '1' is remaining and user pressed backspace and so we clear
        // the edit text.
        if (allDigitString.equals("92") && clearFlag) {
            text.clear();
            clearFlag = false;
            return "";
        }

        if (allDigitString.startsWith("920")) {
            allDigitString = "92";
            formattedString.append("+92-");
            alreadyPlacedDigitCount = alreadyPlacedDigitCount + 2;
        } else if (allDigitString.startsWith("92")) {
            formattedString.append("+92-");
            alreadyPlacedDigitCount = alreadyPlacedDigitCount + 2;
        } else if (allDigitString.startsWith("9")) {
            formattedString.append("+9");
            alreadyPlacedDigitCount++;
        } else if (allDigitString.startsWith("0")) {
            allDigitString = "";
            formattedString.append("+92-");
            alreadyPlacedDigitCount = alreadyPlacedDigitCount + 2;
        } else {
            formattedString.append("+92-");
            alreadyPlacedDigitCount = alreadyPlacedDigitCount + 2;
            formattedString.append(allDigitString);
            alreadyPlacedDigitCount = alreadyPlacedDigitCount + 2;
        }

        // The first 3 numbers beyond '1' must be enclosed in brackets "()"
//        if (totalDigitCount - alreadyPlacedDigitCount > 3) {
//            formattedString.append("(").append(allDigitString.substring(alreadyPlacedDigitCount,
//                    alreadyPlacedDigitCount + 3)).append(") ");
//            alreadyPlacedDigitCount += 3;
//        }
        // There must be a '-' inserted after the next 3 numbers
        if (totalDigitCount - alreadyPlacedDigitCount > 3) {
            formattedString.append(allDigitString.substring(
                    alreadyPlacedDigitCount, alreadyPlacedDigitCount + 3)).append("-");
            alreadyPlacedDigitCount += 3;
        }
        // All the required formatting is done so we'll just copy the
        // remaining digits.
        if (totalDigitCount > alreadyPlacedDigitCount) {
            formattedString.append(allDigitString
                    .substring(alreadyPlacedDigitCount));
        }

        text.clear();
        text.append(formattedString.toString());
        return formattedString.toString();
    }

    public void setOnPhoneNumberTextChangeListener(OnPhoneNumberTextWatcher onPhoneNumberTextChangeListener) {
        this.onPhoneNumberTextWatcher = onPhoneNumberTextChangeListener;
    }

    interface OnPhoneNumberTextWatcher {
        void afterPhoneNumberTextChange(Editable s);

        void beforePhoneNumberTextChange(CharSequence s, int start, int count, int after);
        // void onPhoneNumberTextTextChanged(CharSequence s, int start, int before, int count);
    }
}

