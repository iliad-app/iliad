package com.fast0n.ipersonalarea.ConsumptionDetailsActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import com.fast0n.ipersonalarea.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class DataModel extends ChildViewHolder {

    private final TextView a;
    private final TextView b;
    private final TextView c;
    private final TextView d;
    private final TextView e;
    private final TextView f;

    DataModel(View itemView) {
        super(itemView);

        a = itemView.findViewById(R.id.a);
        b = itemView.findViewById(R.id.b);
        c = itemView.findViewById(R.id.c);
        d = itemView.findViewById(R.id.d);
        e = itemView.findViewById(R.id.e);
        f = itemView.findViewById(R.id.f);

    }

    public void onBind(ModelChildren modelChildren) {


        a.setText(modelChildren.getA().replaceAll("\\s+", ""));
        b.setText(modelChildren.getB());
        c.setText(modelChildren.getC().replaceAll("\\s+", ""));
        d.setText(getContactName(modelChildren.getD().replaceAll("\\s+", "")));
        e.setText(modelChildren.getE().replaceAll("\\s+", ""));
        f.setText(modelChildren.getF().replaceAll("\\s+", ""));


    }

    private String getContactName(String number) {
        String name = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        ContentResolver contentResolver = itemView.getContext().getContentResolver();
        try {
            Cursor contactLookup = contentResolver.query(uri, null, null, null, null);
            try {
                if (contactLookup != null && contactLookup.getCount() > 0) {
                    contactLookup.moveToNext();
                    name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                }else{
                    name = number;
                }
            } finally {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }
        }
        catch (Exception ignored){ name = number;}

        return name;
    }
}
