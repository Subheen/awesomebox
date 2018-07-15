package theawesomebox.com.app.awesomebox.apps.module.ui.home;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.models.backup.Path;


public class BackupDataAdapter extends RecyclerView.Adapter<BackupDataAdapter.BackupViewHolder> {

    private final List<Path> pathList;
    Context context;

    public BackupDataAdapter(List<Path> pathList, BackupDetailsFragment backupDetailsFragment, FragmentActivity activity) {

        this.context = activity;
        this.pathList = pathList;
    }


    @Override
    public BackupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_backup_files, parent, false);
        return new BackupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BackupViewHolder holder, int position) {
        if (holder != null) {
            if (pathList.get(position).getType().matches("folder"))
                holder.backupType.setImageResource(R.drawable.ic_action_folder);
            else
                holder.backupType.setImageResource(R.drawable.ic_action_file);

            holder.backupFileName.setText(pathList.get(position).getName());
            holder.backupFilePath.setText(pathList.get(position).getLocation());
            holder.backupDate.setText(pathList.get(position).getLastBackup());
        }
    }

    @Override
    public int getItemCount() {
        if (pathList != null)
            return pathList.size();
        else
            return 0;
    }


    public class BackupViewHolder extends RecyclerView.ViewHolder {
        ImageView backupType;
        TextView backupFileName, backupFilePath, backupDate;

        public BackupViewHolder(View itemView) {
            super(itemView);

            backupType = itemView.findViewById(R.id.backup_file_type);
            backupFileName = itemView.findViewById(R.id.backup_file_name);
            backupFilePath = itemView.findViewById(R.id.backup_file_path);
            backupDate = itemView.findViewById(R.id.backup_file_date);

        }
    }
}
