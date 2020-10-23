package com.example.chat;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomFound extends ArrayAdapter<String> {

    private String[] itemNames, desc, dates, uids, uploader_names, picture;
    private Activity context;
    private StorageReference storageReference;
    private Uri uri;
    private String url;

    public CustomFound(Activity context, ArrayList<String> itemNames, ArrayList<String> desc, ArrayList<String> dates, ArrayList<String> uids, ArrayList<String> uploaderNames, ArrayList<String> picture ) {
        super(context, R.layout.listview_layout,itemNames);
        storageReference = FirebaseStorage.getInstance().getReference();

        this.context = context;

        this. itemNames = new String[itemNames.size()];
        for(int i = 0; i < itemNames.size(); i ++)
        {
            this.itemNames[i] = itemNames.get(i);
        }

        this.desc = new String[desc.size()];
        for(int i = 0; i < desc.size(); i ++)
        {
            this.desc[i] = desc.get(i);
        }

        this.dates = new String[dates.size()];
        for(int i = 0; i < dates.size(); i ++)
        {
            this.dates[i] = dates.get(i);
        }

        this.uids = new String[uids.size()];
        for(int i = 0; i < uids.size(); i ++)
        {
            this.uids[i] = uids.get(i);
        }

        this.picture = new String[picture.size()];
        for(int i = 0; i < picture.size(); i ++)
        {
            this.picture[i] = picture.get(i);
        }

        this.uploader_names = new String[uploaderNames.size()];
        for(int i = 0; i < uploaderNames.size(); i ++)
        {
            this.uploader_names[i] = uploaderNames.get(i);
        }
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;
        ViewHolder viewHolder = null;
        if (r== null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.listview_layout, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) r.getTag();
        }
        //viewHolder.iv.setImageResource(imgId[position]);
        viewHolder.tv1.setText("Item name: " + itemNames[position]);
        viewHolder.tv2.setText("Description: " + desc[position]);
        viewHolder.tv3.setText("Date of upload: " + dates[position]);
        viewHolder.tv4.setText("UID: " + uids[position]);
        viewHolder.tv5.setText("Uploader name: " + uploader_names[position]);

        //could not add image properly
        url = picture[position];
        Task<Uri> urlTask = storageReference.child("https:/").child("chat-eba03.firebaseio.com/").child("SellPost/").child(url).getDownloadUrl().continueWithTask(new Continuation<Uri, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<Uri> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.child(url).getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    uri = task.getResult();
                    if (uri == null)
                        return;
                }

            }
        });
        Picasso.get().load(uri).into(viewHolder.iv);
        return r;
    }
    class ViewHolder {
        TextView tv1, tv2, tv3, tv4, tv5;
        ImageView iv;

        ViewHolder(View v)
        {
            tv1 = v.findViewById(R.id.itemName);
            tv2 = v.findViewById(R.id.description);
            tv3 = v.findViewById(R.id.extra);
            tv4 = v.findViewById(R.id.uid);
            tv5 = v.findViewById(R.id.uploader_name);
            iv = v.findViewById(R.id.image);
        }
    }
}
