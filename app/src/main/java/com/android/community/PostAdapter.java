package com.android.community;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.community.models.Account;
import com.android.community.models.Post;

import java.util.ArrayList;

import javax.crypto.spec.PSource;

/**
 * Created by adamc on 3/14/17.
 */

public class PostAdapter extends BaseAdapter {
		private static LayoutInflater inflater = null;
		ArrayList<Post> messageList;
		ArrayList<Account> userList;

	public PostAdapter(Activity activity, ArrayList<Post> list, ArrayList<Account> accountList) {
		messageList = list;
		userList = accountList;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Post message = (Post) messageList.get(position);
		Account account = (Account) userList.get(position);
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.chatbubble, null);

		TextView msg = (TextView) vi.findViewById(R.id.message_text_chat);
		msg.setText(message.text);
		TextView username = (TextView) vi.findViewById(R.id.username_chat);
		username.setText(account.getUsername());
		LinearLayout layout = (LinearLayout) vi.findViewById(R.id.bubble_layout);
		LinearLayout parent_layout = (LinearLayout) vi.findViewById(R.id.bubble_layout_parent);

		layout.setBackgroundResource(R.drawable.rounded_corner);
		parent_layout.setGravity(Gravity.LEFT);

		msg.setTextColor(Color.BLACK);

		return vi;
	}

	public void add(Post object, Account accountObj) {
		messageList.add(object);
		userList.add(accountObj);
	}

	public void remove() {
		messageList.clear();
	}
}
