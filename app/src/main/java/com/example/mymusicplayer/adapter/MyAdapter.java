package com.example.mymusicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.po.Song;

import org.w3c.dom.Text;

import java.util.List;


/**
 * Created by Chasen on 2017/4/9.
 */

public class MyAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Song> list;

    public MyAdapter(Context context, List<Song> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song= (Song) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_music_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.item_mymusic_song);
            viewHolder.artistTextView = (TextView) view.findViewById(R.id.item_mymusic_artist);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.titleTextView.setText(song.getTitle());
        viewHolder.artistTextView.setText(song.getArtist());
        return view;
    }

    class ViewHolder {

        TextView titleTextView;

        TextView artistTextView;
    }
}
