package theawesomebox.com.app.awesomebox.apps.module.ui.scripts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.models.scripts.Result;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;


public class ScriptsAdapter extends RecyclerView.Adapter<ScriptsAdapter.ScriptsViewHolder> {

    private final ClickListener listener;
    private final List<Result> scriptsList;
    private Context context;
    private String scriptType;

    public ScriptsAdapter(List<Result> scriptsList, DefaultScriptFragment defaultScriptFragment, FragmentActivity activity, String scriptType, ClickListener listener) {
        this.context = activity;
        this.scriptType = scriptType;
        this.scriptsList = scriptsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScriptsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_scripts, parent, false);
        return new ScriptsViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ScriptsViewHolder holder, int position) {


        holder.txtScriptType.setText(scriptsList.get(position).getScriptName() + "");
        holder.txtScriptDesc.setText(scriptsList.get(position).getScriptDescription() + "");
        String colorScript = scriptsList.get(position).getScriptFilter().get(0).getValue();

        if (AppUtils.ifNotNullEmpty(colorScript)) {
            if (colorScript.equalsIgnoreCase("default")) {
                holder.colorStrip.setBackgroundColor(context.getResources().getColor(R.color.script_yellow));
            } else if (colorScript.equalsIgnoreCase("favorite")) {
                holder.colorStrip.setBackgroundColor(context.getResources().getColor(R.color.script_green));
                holder.imgScriptType.setImageResource(R.drawable.favourite);
            } else if (colorScript.equalsIgnoreCase("optimize")) {
                holder.colorStrip.setBackgroundColor(context.getResources().getColor(R.color.script_green));
                holder.imgScriptType.setImageResource(R.drawable.optimize);
            } else if (colorScript.equalsIgnoreCase("security")) {
                holder.colorStrip.setBackgroundColor(context.getResources().getColor(R.color.script_red));
                holder.imgScriptType.setImageResource(R.drawable.security);
            } else if (colorScript.equalsIgnoreCase("health")) {
                holder.colorStrip.setBackgroundColor(context.getResources().getColor(R.color.script_yellow));
                holder.imgScriptType.setImageResource(R.drawable.health);
            } else if (colorScript.equalsIgnoreCase("network")) {
                holder.colorStrip.setBackgroundColor(context.getResources().getColor(R.color.script_blue));
                holder.imgScriptType.setImageResource(R.drawable.network);
            } else if (colorScript.equalsIgnoreCase("connection")) {
                holder.colorStrip.setBackgroundColor(context.getResources().getColor(R.color.script_purple));
                holder.imgScriptType.setImageResource(R.drawable.connection);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (scriptsList != null && scriptsList.size() > 0)
            return scriptsList.size();
        else
            return 0;
    }


    class ScriptsViewHolder extends RecyclerView.ViewHolder {
        Button btnRunScript;
        TextView colorStrip, txtScriptType, txtScriptDesc, txtScriptConfig;
        ImageView imgScriptType;
        private WeakReference<ClickListener> listenerRef;

        ScriptsViewHolder(View itemView, final ClickListener listener) {
            super(itemView);
            btnRunScript = itemView.findViewById(R.id.btn_run_script);
            colorStrip = itemView.findViewById(R.id.color_strip);
            txtScriptType = itemView.findViewById(R.id.txt_script_type);
            txtScriptDesc = itemView.findViewById(R.id.txt_script_desc);
            txtScriptConfig = itemView.findViewById(R.id.txt_script_config);
            imgScriptType = itemView.findViewById(R.id.img_script_type);
            listenerRef = new WeakReference<>(listener);

            btnRunScript.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPositionClicked(view, getAdapterPosition());
                }
            });
        }
    }
}
