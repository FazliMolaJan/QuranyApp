package education.mahmoud.quranyapp.feature.show_sura_ayas;

import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import education.mahmoud.quranyapp.R;
import education.mahmoud.quranyapp.Util.Data;
import education.mahmoud.quranyapp.Util.Util;
import education.mahmoud.quranyapp.data_layer.local.room.AyahItem;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.Holder> {

    int ayahsColor, scrollColor;
    int vis = View.INVISIBLE;
    private List<Page> list = new ArrayList<>();
    private Typeface typeface;
    private IOnClick iOnClick;
    private PageShown pageShown;
    private IBookmark iBookmark;

    public PageAdapter(Typeface typeface, int ayahsColor, int scrollColor) {
        this.typeface = typeface;
        this.ayahsColor = ayahsColor;
        this.scrollColor = scrollColor;
    }

    public void setPageShown(PageShown pageShown) {
        this.pageShown = pageShown;
    }

    public void setiBookmark(IBookmark iBookmark) {
        this.iBookmark = iBookmark;
    }

    public void setiOnClick(IOnClick iOnClick) {
        this.iOnClick = iOnClick;
    }

    public void setPageList(List<Page> newList) {
        list = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public List<Page> getList() {
        return list;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_item, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Page item = list.get(i);

        holder.imBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iBookmark.onBookmarkClicked(item);
            }
        });

        // set Colors
        holder.tvAyahs.setTextColor(ayahsColor);
        holder.scAyahsText.setBackgroundColor(scrollColor);


        StringBuilder builder = new StringBuilder();
        String aya;
        String suraName = "";
        suraName = getSuraNameFromIndex(item.getAyahItems().get(0).getSurahIndex());

        String tempSuraName;
        boolean isFirst = true;
        for (AyahItem ayahItem : item.getAyahItems()) {
            aya = ayahItem.getText();
            // add sura name
            if (ayahItem.getAyahInSurahIndex() == 1) {
                tempSuraName = getSuraNameFromIndex(ayahItem.getSurahIndex());
                if (isFirst) {
                    // handle first name in page that not need a previous new line
                    builder.append(tempSuraName + "\n");
                } else {
                    builder.append("\n" + tempSuraName + "\n");
                }
                //// TODO: 5/16/2019 splist basmallah
            }
            isFirst = false;
            builder.append(aya + " ﴿ " + ayahItem.getAyahInSurahIndex() + " ﴾ ");

        }


        holder.tvAyahs.setText(Util.getSpannable(builder.toString()), TextView.BufferType.SPANNABLE);
        holder.tvAyahs.setTypeface(typeface);
        // text justifivation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.tvAyahs.setJustificationMode(Layout.JUSTIFICATION_MODE_NONE);
        }

        // top - bottom details
        holder.tvPageNumShowAyahs.setText(String.valueOf(item.getPageNum()));
        holder.tvSurahName.setText(suraName);
        // holder.tvJuz.setText(Util.getArabicStrOfNum(item.getJuz()));
        holder.tvJuz.setText(String.valueOf(item.getJuz()));

        // handle click to show/hide info
        holder.topLinear.setOnClickListener(e -> {
            vis = holder.topLinear.getVisibility();
            vis = vis == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
            holder.BottomLinear.setVisibility(vis);
            holder.topLinear.setVisibility(vis);
        });

        holder.scAyahsText.setOnClickListener((v) -> {
            iOnClick.onClick(holder.getAdapterPosition());
        });
    }

    /**
     * @param surahIndex in quran
     * @return
     */
    private String getSuraNameFromIndex(int surahIndex) {
        return Data.SURA_NAMES[surahIndex - 1];
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Holder holder) {
        super.onViewAttachedToWindow(holder);
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                vis = View.VISIBLE;
                holder.BottomLinear.setVisibility(vis);
                holder.topLinear.setVisibility(vis);
            }
        }.start();
        pageShown.onDiplayed(holder.getAdapterPosition(), holder);
    }

    interface PageShown {
        void onDiplayed(int pos, Holder holder);
    }

    interface IBookmark {
        void onBookmarkClicked(Page item);
    }

    class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvAyahs)
        TextView tvAyahs;
        @BindView(R.id.sc_ayahs_text)
        ScrollView scAyahsText;
        @BindView(R.id.tvSurahName)
        TextView tvSurahName;
        @BindView(R.id.tvJuz)
        TextView tvJuz;
        @BindView(R.id.imBookmark)
        ImageView imBookmark;
        @BindView(R.id.topLinear)
        LinearLayout topLinear;
        @BindView(R.id.tvPageNumShowAyahs)
        TextView tvPageNumShowAyahs;
        @BindView(R.id.BottomLinear)
        LinearLayout BottomLinear;
        @BindView(R.id.ayahsLayout)
        FrameLayout ayahsLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}