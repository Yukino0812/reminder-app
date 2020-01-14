package me.yukino.reminderapp.record.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.yukino.reminderapp.R;
import me.yukino.reminderapp.record.vo.RecordVO;

/**
 * @author Yukino Yukinoshita
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<RecordVO> recordVOList;
    private RecordOnClickListener listener;
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss E", Locale.CHINA);

    public RecordAdapter() {
        recordVOList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.bind(recordVOList.get(position));
    }

    @Override
    public int getItemCount() {
        return recordVOList.size();
    }

    public void refreshList(List<RecordVO> vos) {
        this.recordVOList = vos;
        notifyDataSetChanged();
    }

    public void setRecordOnClickListener(RecordOnClickListener listener) {
        this.listener = listener;
    }

    public interface RecordOnClickListener {

        void onClickRecord(RecordVO vo);

        void onClickDeleteRecord(RecordVO vo);

    }

    class RecordViewHolder extends RecyclerView.ViewHolder {

        private View view;

        @BindView(R.id.tvSubject)
        TextView tvSubject;
        @BindView(R.id.tvDetail)
        TextView tvDetail;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.ivDelete)
        ImageView ivDelete;

        RecordViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        void bind(RecordVO vo) {
            tvSubject.setText(vo.getSubject());
            tvDetail.setText(vo.getDetails());
            tvTime.setText(format.format(vo.getTime()));

            view.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickRecord(vo);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickDeleteRecord(vo);
                }
            });
        }

    }

}
