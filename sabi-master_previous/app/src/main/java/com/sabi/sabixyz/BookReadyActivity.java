package com.sabi.sabixyz;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sabi.sabixyz.effects.TabletPageTransformer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class BookReadyActivity extends AppCompatActivity implements View.OnClickListener {
    String[] pageData;
    String[] DataContent;
    ArrayList<String> content_data = new ArrayList<String>();
    String content, description, imageurl;
    LayoutInflater inflater;
    TextView book_text_content, pageCounter;
    ImageView coverImage, mNightMode, mIncreaseFont, mDecreaseFont;
    LinearLayout mControls, mPagerContainer;
    SeekBar bookseekpages;
    ViewPager vp;
    View page;
    int NightMode_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ready);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //pageData=getResources().getStringArray(R.array.desserts);

        content = getIntent().getStringExtra("content");
        description = getIntent().getStringExtra("desc");
        imageurl = getIntent().getStringExtra("imageurl");


        pageData = content.split("<page>");
        DataContent = new String[pageData.length+1];
        DataContent[0]=imageurl;
        //content_data.add(imageurl);
        for(int a=1;a<=pageData.length; a++){
        DataContent[a] = pageData[a-1];
        }
        Log.e("content", DataContent[1]);
        vp = findViewById(R.id.viewPagerBook);


        mControls = findViewById(R.id.linearlayout_controls);
        pageCounter = findViewById(R.id.tv_page_counter);
        mNightMode = findViewById(R.id.img_night_mode);
        mIncreaseFont = findViewById(R.id.img_font_increase);
        mDecreaseFont = findViewById(R.id.img_font_decrease);
        mIncreaseFont.setOnClickListener(this);
        mDecreaseFont.setOnClickListener(this);
        mNightMode.setOnClickListener(this);
        mPagerContainer = findViewById(R.id.linearlayout_viewpager_container);

        bookseekpages = findViewById(R.id.bookseekpages);
        bookseekpages.setMax( (DataContent.length - 0) / 1 );
        myBookPagerAdapter adapter = new myBookPagerAdapter(getBaseContext(), "#000000", "");
        vp.setAdapter(adapter);
        vp.setPageTransformer(true, new TabletPageTransformer());

        NightMode_ = 0;
        bookseekpages.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vp.setCurrentItem(progress, true);
                pageCounter.setText(progress+"/"+DataContent.length);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setNightMode(String textColor, String BackgroundColor) {
        book_text_content = getNewTv();
        int currentItem= vp.getCurrentItem();
        vp.setAdapter(new myBookPagerAdapter(getBaseContext(), textColor, ""));
        vp.setCurrentItem(currentItem);
        mPagerContainer.setBackgroundColor(Color.parseColor(BackgroundColor));
    }


    private TextView getNewTv() {
        page = inflater.inflate(R.layout.book_reader_layout, null);
        TextView content = page.findViewById(R.id.text_content);
        return content;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_night_mode:
                if(NightMode_==0){
                    setNightMode("#ffffff", "#000000");
                    NightMode_=1;
                }else{
                    setNightMode("#000000", "#ffffff");
                    NightMode_=0;
                }
                break;
            case R.id.img_font_increase:
                Log.e("increasefont", "increased");
                int currentItem= vp.getCurrentItem();
                //int currentScroll = vp.getScrollY();
                vp.setAdapter(new myBookPagerAdapter(getBaseContext(), "", "+"));
                vp.setCurrentItem(currentItem);

                //vp.scrollTo(0, currentScroll);
                //vp.getChildAt(vp.getCurrentItem()).setScrollY(100);

                break;
        }
    }

    private class myBookPagerAdapter extends PagerAdapter implements View.OnClickListener {
        private String color="#000000";
        private String operator="";

        public myBookPagerAdapter(Context context, String settings_colour, String operation){
            if(!settings_colour.equals("")) {
                this.color = settings_colour;
            }
            if(!operation.equals("")) {
                this.operator = operation;
            }
        }

        @Override
        public int getCount() {
            //Return total pages, here one for each data item
            return DataContent.length;
        }


        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //page = inflater.inflate(R.layout.book_reader_layout, null);

            book_text_content = getNewTv();
            //book_text_content = page.findViewById(R.id.text_content);
            book_text_content.setOnClickListener(this);

            if(position==0){
                coverImage = page.findViewById(R.id.imgview_book_cover);
                Picasso.get().load(getResources().getString(R.string.localpath)+imageurl).into(coverImage);
                coverImage.setVisibility(View.VISIBLE);
                book_text_content.setVisibility(View.GONE);
            }else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    book_text_content.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    book_text_content.setText(Html.fromHtml(DataContent[position], Html.FROM_HTML_MODE_COMPACT));
                    //textView.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>", Html.FROM_HTML_MODE_COMPACT));
                } else {
                    //book_text_content.setText(pageData[position]);
                    book_text_content.setText(Html.fromHtml(DataContent[position]));
                }

            }
            //bookseekpages.setProgress(position);

            if(operator.equals("+")){
                int newSize = (int) book_text_content.getTextSize()+1;
                book_text_content.setTextSize(newSize);
            }else if(operator.equals("-")){
                int newSize = (int) book_text_content.getTextSize()-1;
                book_text_content.setTextSize(newSize);
            }

            if(!this.color.equals("")) {
                int settings_color = Color.parseColor(color);
                book_text_content.setTextColor(settings_color);
            }

            pageCounter.setText(position+"/"+getCount());


            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);
            //container.setScroll
            return page;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //See if object from instantiateItem is related to the given view
            //required by API
            return arg0==(View)arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object=null;
        }

        @Override
        public void onClick(View v) {
            //BookReadyActivity.mBoook

            if(mControls.getVisibility()== View.VISIBLE) {
                mControls.setVisibility(View.GONE);
            }else{
                mControls.setVisibility(View.VISIBLE);
            }

        }
    }


}
