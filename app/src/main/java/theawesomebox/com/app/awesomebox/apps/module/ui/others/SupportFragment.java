package theawesomebox.com.app.awesomebox.apps.module.ui.others;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.common.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends BaseFragment implements View.OnClickListener {


    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindListeners(view);
    }

    private void bindListeners(View view) {
        view.findViewById(R.id.btn_call).setOnClickListener(this);
        view.findViewById(R.id.btn_mail).setOnClickListener(this);
    }

    @Override
    public String getTitle() {
        return getString(R.string.suport);
    }

    public void openEmailIntent() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "AwesomeBox Support");

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Please describe your issue here!");
        emailIntent.setData(Uri.parse("mailto: support@awesomebox.tech"));
        startActivity(Intent.createChooser(emailIntent, "AwesomeBox Support"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
                showCancelJobDialog();
                break;
            case R.id.btn_mail:
                openEmailIntent();
                break;
        }
    }

    private void showCancelJobDialog() {

        Button bntNo, btnYes;
        TextView txtPrompt;

        final Dialog cancelDialog = new Dialog(getActivity(), R.style.full_screen_dialog);
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancelDialog.setContentView(R.layout.dialog_response);
        cancelDialog.setCancelable(true);
        cancelDialog.setCanceledOnTouchOutside(true);
        final Window window = cancelDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        bntNo = cancelDialog.findViewById(R.id.dialog_no);
        btnYes = cancelDialog.findViewById(R.id.dialog_yes);
        txtPrompt = cancelDialog.findViewById(R.id.txt_prompt);

        bntNo.setText("Cancel");
        btnYes.setText("Call");

        txtPrompt.setText("+1(872)240-2269");


        bntNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
                openCallDialer("+1(872)240-2269");
            }
        });

        cancelDialog.show();
    }
    private void openCallDialer(String cellNumber) {
        String cellNumberString = "tel:" + cellNumber;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(cellNumberString));
        startActivity(intent);
    }
}
