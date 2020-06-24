package com.example.sabixyz.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sabixyz.Sabi_Constants;
import com.example.sabixyz.MainActivity;
import com.example.sabixyz.R;
import com.example.sabixyz.model.CategoriesList;
import com.example.sabixyz.tabs.index1Fragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder> {

    private List<CategoriesList> listitems;
    private Context context;
    private int clickType;

    public CategoriesRecyclerAdapter(List<CategoriesList> listitems, Context context, int clickType) {

        this.listitems = listitems;
        this.context = context;
        this.clickType = clickType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.categories_list_item, viewGroup, false);
        return new CategoriesRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesRecyclerAdapter.ViewHolder holder, int i) {
        final CategoriesList listItem = listitems.get(i);
        holder.textViewTitle.setText(listItem.getTitle());
        holder.textViewShortDesc.setText(listItem.getShortDescription());
        holder.CategoryID = listItem.getId();
        holder.list = listItem;
        Log.e("allurl", Sabi_Constants.LOCAL_PATH+listItem.getImageUrl());
        Picasso.get().load(Sabi_Constants.LOCAL_PATH +listItem.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textViewTitle;
        public TextView textViewShortDesc;
        public ImageView imageView;
        public String CategoryID;
        public CategoriesList list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            imageView = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View view)
        {
            // get the position on recyclerview.
            int pos = getLayoutPosition();

            open_category_book(view, pos, list);

        }

        private void open_category_book(View view, int pos, CategoriesList listItem) {
            Log.e("Item", "Itemclicked"+pos);
            Bundle b = new Bundle();
            b.putString("ID", list.getId());
            b.putString("description", list.getDescription());
            b.putString("short_desc", list.getShortDescription());
            b.putString("imageurl", list.getImageUrl());
            b.putString("title", list.getTitle());
            b.putString("api_to_call", "get_category_books");
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment myFragment = new index1Fragment();
            myFragment.setArguments(b);
            MainActivity.tv_header_title.setText(list.getTitle());
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack("categories_books").commit();

        }



    }
}
