package com.frank.lib_picturepicker.picturepicker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.frank.lib_picturepicker.R;
import com.frank.lib_picturepicker.widget.PictureIndicatorView;

import java.util.List;

/**
 * Created by think on 2018/5/26.
 * Email: frankchoochina@gmail.com
 * Version: 1.2
 * Description: 图片选择器的 Adapter
 */
public class PicturePickerAdapter extends RecyclerView.Adapter<PicturePickerAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mUris;
    private AdapterInteraction mInteraction;
    private int mIndicatorSolidColor;
    private int mIndicatorBorderCheckedColor;
    private int mIndicatorBorderUncheckedColor;

    // 用于延时更新角标
    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private Runnable mRefreshIndicatorRunnable = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    public interface AdapterInteraction {

        List<String> getPickedPictures();

        boolean onPicturePicked(String imagePath);

        void onPictureRemoved(String imagePath);

    }

    public PicturePickerAdapter(Context context, List<String> uris,
                                int indicatorSolidColor,
                                int indicatorBorderCheckedColor,
                                int indicatorBorderUncheckedColor) {
        this.mContext = context;
        this.mUris = uris;
        this.mInteraction = (AdapterInteraction) context;
        this.mIndicatorSolidColor = indicatorSolidColor;
        this.mIndicatorBorderCheckedColor = indicatorBorderCheckedColor;
        this.mIndicatorBorderUncheckedColor = indicatorBorderUncheckedColor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycle_item_activity_picture_picker, parent, false);
        // 将 ItemView 的高度修正为宽度 parent 的宽度的三分之一
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.height = parent.getMeasuredWidth() / 3;
        itemView.setLayoutParams(params);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String uri = mUris.get(position);
        Glide.with(mContext).load(uri).into(holder.ivPicture);
        // 判断当前 uri 是否被选中了
        final int index = mInteraction.getPickedPictures().indexOf(uri);
        holder.checkIndicator.setChecked(index != -1);
        holder.checkIndicator.setText(String.valueOf(mInteraction.getPickedPictures().indexOf(uri) + 1));
        // 设置点击监听器
        holder.checkIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkIndicator.isChecked()) {// Checked-> Unchecked
                    // 移除选中数据与状态
                    mInteraction.onPictureRemoved(uri);
                    holder.checkIndicator.setChecked(false);
                    // 需要延时的更新索引角标
                    notifyCheckedIndicatorChanged();
                } else {// Unchecked -> Checked
                    // 判断是否达到选择上限
                    holder.checkIndicator.setChecked(mInteraction.onPicturePicked(uri));
                    // 设置文本
                    holder.checkIndicator.setText(String.valueOf(mInteraction.getPickedPictures().size()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUris.size();
    }

    /**
     * 通知选中图片的角标变更
     */
    private void notifyCheckedIndicatorChanged() {
        mMainThreadHandler.postDelayed(mRefreshIndicatorRunnable, 300);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView ivPicture;
        final PictureIndicatorView checkIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.iv_picture);
            checkIndicator = itemView.findViewById(R.id.check_indicator);
            checkIndicator.setSolidColor(mIndicatorSolidColor);
            checkIndicator.setBorderColor(mIndicatorBorderCheckedColor, mIndicatorBorderUncheckedColor);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkIndicator.callOnClick();
                }
            });
        }
    }

}
