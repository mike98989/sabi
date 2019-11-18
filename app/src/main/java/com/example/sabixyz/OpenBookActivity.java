package com.example.sabixyz;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sabixyz.effects.HorizontalFlip;
import com.example.sabixyz.effects.RotateDown;
import com.example.sabixyz.effects.TabletPageTransformer;
import com.folioreader.FolioReader;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class OpenBookActivity extends AppCompatActivity implements View.OnClickListener {
private String content, description, imageurl;
private LayoutInflater inflater;
    String[] pageData;
    String[] DataContent;
    public TextView tv_book_content, tv_book_content_parent,pageCounter;
    LinearLayout mControls, mPagerContainer;
    ImageView coverImage, mNightMode, mIncreaseFont, mDecreaseFont;
    ViewPager vp, container;
    View page, parent;
    SparseArray<View> views = new SparseArray<View>();
    float currentFontSize=18;
    int NightMode_, currentItem_;
    //String color="#ffffff";
    String color="#000000";
    String BackgroundColor = "#ffffff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);
        FolioReader folioReader = FolioReader.get();
        folioReader.openBook("file:///asset/epub2.epub");

        //page = inflater.inflate(R.layout.book_reader_layout, null);
        //parent = inflater.inflate(R.layout.activity_open_book, null);
        //tv_book_content_parent = findViewById(R.id.text_content);

        //coverImage = page.findViewById(R.id.imgview_book_cover);

        /*
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        content = getIntent().getStringExtra("content");
        description = getIntent().getStringExtra("desc");
        imageurl = getIntent().getStringExtra("imageurl");
        pageData = content.split("<page>");
        DataContent = new String[pageData.length+1];
        DataContent[0]=imageurl;
        for(int a=1;a<=pageData.length; a++){
            DataContent[a] = pageData[a-1];
        }

        mControls = findViewById(R.id.linearlayout_controls);
        pageCounter = findViewById(R.id.tv_page_counter);
        mNightMode = findViewById(R.id.img_night_mode);
        mIncreaseFont = findViewById(R.id.img_font_increase);
        mDecreaseFont = findViewById(R.id.img_font_decrease);
        mIncreaseFont.setOnClickListener(this);
        mDecreaseFont.setOnClickListener(this);
        mNightMode.setOnClickListener(this);
        mPagerContainer = findViewById(R.id.linearlayout_viewpager_container);
        //currentFontSize = tv_book_content_parent.getTextSize();
        vp = findViewById(R.id.openBookPager);
        OpenBookActivity.myBookPagerAdapter adapter = new OpenBookActivity.myBookPagerAdapter();
        vp.setAdapter(adapter);
        vp.setPageTransformer(true, new TabletPageTransformer());
        //int currentpage = vp.getCurrentItem();
        //Picasso.get().load(getResources().getString(R.string.localpath)+imageurl).into(coverImage);
        //coverImage.setVisibility(View.VISIBLE);
        //tv_book_content_parent.setVisibility(View.GONE);
        NightMode_=0;
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                int currentItem = vp.getCurrentItem();
                //tv_book_content_parent.setText(DataContent[currentpage]);
                updateView(currentItem);
                //updateView(currentpage-1);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        */

    }



    private void updateView(int currentPage){
        coverImage = findViewById(R.id.imgview_book_cover);
        tv_book_content_parent = findViewById(R.id.text_content);
        tv_book_content_parent.setOnClickListener(this);
        //tv_book_content_parent.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentFontSize);

        if(currentPage==0){
            Picasso.get().load(getResources().getString(R.string.localpath)+imageurl).into(coverImage);
            coverImage.setVisibility(View.VISIBLE);
            tv_book_content_parent.setVisibility(View.GONE);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tv_book_content_parent.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv_book_content_parent.setText(Html.fromHtml(DataContent[currentPage], Html.FROM_HTML_MODE_COMPACT));
                //textView.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>", Html.FROM_HTML_MODE_COMPACT));
            } else {
                //book_text_content.setText(pageData[position]);
                tv_book_content_parent.setText(Html.fromHtml(DataContent[currentPage]));
            }

            //Log.e("Content", tv_book_content_parent.getText().toString());

        }
        Log.e("Page", currentPage+"");
        int settings_color = Color.parseColor(color);
        tv_book_content_parent.setTextColor(settings_color);
        tv_book_content_parent.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentFontSize);
        mPagerContainer.setBackgroundColor(Color.parseColor(BackgroundColor));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_font_increase:
                //float currentSize = tv_book_content_parent.getTextSize()*1.1f;
                currentFontSize=currentFontSize*1.1f;
                tv_book_content_parent.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentFontSize);
                currentItem_ = vp.getCurrentItem();
                updateView(currentItem_);
                //vp.setCurrentItem(currentItem_);
                break;
            case R.id.img_font_decrease:
                //float currentSize2 = tv_book_content_parent.getTextSize()/1.1f;
                currentFontSize=currentFontSize/1.1f;
                tv_book_content_parent.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentFontSize);
                //currentItem_ = vp.getCurrentItem();
                //vp.setCurrentItem(currentItem_);
                break;
            case R.id.img_night_mode:

                if(NightMode_==0){
                    color="#000000";
                    BackgroundColor = "#ffffff";
                    int settings_color = Color.parseColor(color);
                    tv_book_content_parent.setTextColor(settings_color);
                    mPagerContainer.setBackgroundColor(Color.parseColor(BackgroundColor));
                    NightMode_=1;
                    int currentItem= vp.getCurrentItem();
                    vp.setCurrentItem(currentItem);
                }else{
                    color="#ffffff";
                    BackgroundColor = "#000000";
                    int settings_color = Color.parseColor(color);
                    tv_book_content_parent.setTextColor(settings_color);
                    mPagerContainer.setBackgroundColor(Color.parseColor(BackgroundColor));
                    NightMode_=0;
                    int currentItem= vp.getCurrentItem();
                    //vp.setAdapter(new BookReadyActivity.myBookPagerAdapter(getBaseContext(), textColor, ""));
                    vp.setCurrentItem(currentItem);

                }

                break;
            case R.id.text_content:
                if(mControls.getVisibility()== View.VISIBLE) {
                    mControls.setVisibility(View.GONE);
                }else{
                    mControls.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    private class myBookPagerAdapter  extends PagerAdapter {

        public myBookPagerAdapter() {

        }

        @Override
        public int getCount() {
            return DataContent.length;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            page = inflater.inflate(R.layout.book_reader_layout, null);
            //tv_book_content_parent = page.findViewById(R.id.text_content);
            //tv_book_content_parent.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentFontSize);
            //Add the page to the front of the queue
            container.addView(page, 0);
            //container.setScroll
            views.put(position, page);
            return page;
        }


        @Override
        public void notifyDataSetChanged() {
            int key = 0;
            for(int i = 0; i < views.size(); i++) {
                key = views.keyAt(i);
                View view = views.get(key);
            }
            super.notifyDataSetChanged();
        }



        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==(View)arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            views.remove(position);
            object=null;
        }
    }
}
