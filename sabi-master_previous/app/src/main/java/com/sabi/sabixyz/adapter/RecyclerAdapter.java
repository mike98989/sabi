package com.sabi.sabixyz.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.sabi.sabixyz.BookDetailsFragment;
import com.sabi.sabixyz.Constants;
import com.sabi.sabixyz.OpenBookActivity;
import com.sabi.sabixyz.R;
import com.sabi.sabixyz.model.ListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {


    private List<ListItem> listitems;
    private List<ListItem> DataFilteredList;
    private Context context;
    private int clickType;

    public RecyclerAdapter(List<ListItem> data, Context context, int clickType) {
        this.listitems = data;
        this.DataFilteredList = data;
        this.context = context;
        this.clickType = clickType;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final ListItem listItem = DataFilteredList.get(i);
        holder.textViewHead.setText(listItem.getHead());
        holder.textViewAuthor.setText(listItem.getAuthor());
        holder.BookID = listItem.getId();
        holder.list = listItem;
        Picasso.get().load(Constants.LOCAL_PATH+listItem.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return DataFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                if (TextUtils.isEmpty(charSequence)) {
                    results.count = listitems.size();
                    results.values = listitems;
                }else{
                    //Create a new list to filter on
                    String charString = charSequence.toString();
                    List<ListItem> resultList = new ArrayList<>();
                    for (ListItem row : listitems) {
                        if (row.getHead().toLowerCase().contains(charString.toLowerCase()) || row.getAuthor().toLowerCase().contains(charString.toLowerCase())) {
                            resultList.add(row);
                        }
                    }
                    results.count = resultList.size();
                    results.values = resultList;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                //listitems.clear();
                //listitems.addAll((List) filterResults.values);

                //listitems = (ArrayList<ListItem>) filterResults.values;
                //notifyDataSetChanged();

                    DataFilteredList = (ArrayList<ListItem>)results.values;
                    notifyDataSetChanged();

            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textViewHead;
        public TextView textViewAuthor;
        public ImageView imageView;
        public String BookID;
        public ListItem list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewHead = itemView.findViewById(R.id.textViewHead);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            imageView = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View view)
        {
            // get the position on recyclerview.
            int pos = getLayoutPosition();

            if(clickType==1) {
                open_details(view, pos, list);    // click on recyclerview item.
            }else if(clickType==2){
                open_book(view, pos, list);
            }
        }

        private void open_details(View view, int pos, ListItem listItem) {
            Log.e("Item", "Itemclicked"+pos);

            Bundle b = new Bundle();
            b.putString("id", list.getId());
            b.putString("description", list.getDescription());
            b.putString("imageurl", list.getImageUrl());
            b.putString("title", list.getHead());
            b.putString("author",list.getAuthor());
            b.putString("amount", list.getAmount());
            b.putString("list", list.toString());
            b.putString("filepath", list.getFilePath());
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment myFragment = new BookDetailsFragment();
            myFragment.setArguments(b);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();


/*            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, myFragment);
            fragmentTransaction.commit();
            fragmentManager.popBackStack();*/

        }


        private void open_book(View view, int pos, ListItem listItem) {
            //Log.e("list", list.toString());

            Bundle b = new Bundle();
            b.putString("id", list.getId());
            b.putString("description", list.getDescription());
            b.putString("imageurl", list.getImageUrl());
            b.putString("title", list.getHead());
            b.putString("author",list.getAuthor());
            b.putString("amount", list.getAmount());
            b.putString("list", list.toString());
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            //Intent i = new Intent(activity, BookReadyActivity.class);
            Intent i = new Intent(activity, OpenBookActivity.class);
            i.putExtra("imageurl", list.getImageUrl());
            i.putExtra("title", list.getHead());
            i.putExtra("content", list.getContent());
            i.putExtra("desc", list.getDescription());
            i.putExtra("filepath", list.getFilePath());
            activity.startActivity(i);

            /*
            MainActivity.tv_header_title.setText(list.getHead());
            Fragment myFragment = new BookReaderFragment();
            myFragment.setArguments(b);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
            */
        }

    }


}
