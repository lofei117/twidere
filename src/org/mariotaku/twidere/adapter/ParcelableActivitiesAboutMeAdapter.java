package org.mariotaku.twidere.adapter;

import static org.mariotaku.twidere.util.UserColorNicknameUtils.getUserColor;
import static org.mariotaku.twidere.util.UserColorNicknameUtils.getUserNickname;
import static org.mariotaku.twidere.util.Utils.getAccountColor;
import static org.mariotaku.twidere.util.Utils.getStatusBackground;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import org.mariotaku.twidere.R;
import org.mariotaku.twidere.model.ParcelableActivity;
import org.mariotaku.twidere.model.ParcelableLocation;
import org.mariotaku.twidere.model.ParcelableStatus;
import org.mariotaku.twidere.model.ParcelableUser;
import org.mariotaku.twidere.model.ParcelableUserList;
import org.mariotaku.twidere.util.ImageLoaderWrapper;
import org.mariotaku.twidere.util.ImageLoadingHandler;
import org.mariotaku.twidere.util.TwidereLinkify;
import org.mariotaku.twidere.view.holder.ActivityViewHolder;

public class ParcelableActivitiesAboutMeAdapter extends BaseParcelableActivitiesAdapter {

	private final Context mContext;
	private final ImageLoadingHandler mImageLoadingHandler;

	private boolean mGapDisallowed;
	private boolean mIndicateMyStatusDisabled;
	private boolean mFavoritesHighlightDisabled;
	private boolean mDisplayImagePreview;
	private boolean mDisplaySensitiveContents;

	public ParcelableActivitiesAboutMeAdapter(final Context context) {
		super(context);
		mContext = context;
		mImageLoadingHandler = new ImageLoadingHandler();
	}

	@Override
	public void bindView(final int position, final ActivityViewHolder holder, final ParcelableActivity item) {
		if (item == null) return;
		final ParcelableUser[] sources = item.sources;
		final ParcelableStatus[] target_statuses = item.target_statuses;
		final int sources_length = sources != null ? sources.length : 0;
		final int target_statuses_length = target_statuses != null ? target_statuses.length : 0;
		final int action = item.action;
		final boolean displayProfileImage = shouldDisplayProfileImage();

		final TwidereLinkify linkify = getLinkify();
		final int highlightOption = getLinkHighlightOption();

		final Context context = getContext();
		if (sources_length > 0) {
			final ParcelableUser firstSource = sources[0];
			final ParcelableStatus[] target_objects = item.target_object_statuses;
			final String sourceName = getName(firstSource);
			switch (action) {
				case ParcelableActivity.ACTION_FAVORITE: {
					holder.name.setVisibility(View.VISIBLE);
					holder.screen_name.setVisibility(View.GONE);
					holder.profile_image.setVisibility(View.GONE);
					holder.my_profile_image.setVisibility(View.GONE);
					holder.text.setVisibility(View.VISIBLE);
					holder.reply_retweet_status.setVisibility(View.GONE);
					holder.activity_profile_images_container.setVisibility(displayProfileImage ? View.VISIBLE
							: View.GONE);

					holder.name.setSingleLine(false);

					if (target_statuses_length > 0) {
						final ParcelableStatus status = target_statuses[0];
						if (highlightOption != LINK_HIGHLIGHT_OPTION_CODE_NONE) {
							holder.text.setText(Html.fromHtml(status.text_html));
							linkify.applyAllLinks(holder.text, status.account_id, status.is_possibly_sensitive);
							holder.text.setMovementMethod(null);
						} else {
							holder.text.setText(status.text_unescaped);
						}
					}
					if (sources_length == 1) {
						holder.name.setText(context.getString(R.string.activity_about_me_favorite, sourceName));
					} else {
						holder.name.setText(context.getString(R.string.activity_about_me_favorite_multi, sourceName,
								sources_length - 1));
					}
					displayActivityUserProfileImages(holder, sources);
					break;
				}
				case ParcelableActivity.ACTION_FOLLOW: {
					holder.name.setVisibility(View.VISIBLE);
					holder.screen_name.setVisibility(View.GONE);
					holder.profile_image.setVisibility(View.GONE);
					holder.my_profile_image.setVisibility(View.GONE);
					holder.text.setVisibility(View.GONE);
					holder.reply_retweet_status.setVisibility(View.GONE);

					holder.name.setSingleLine(false);

					if (sources_length == 1) {
						holder.name.setText(context.getString(R.string.activity_about_me_follow, sourceName));
					} else {
						holder.name.setText(context.getString(R.string.activity_about_me_follow_multi, sourceName,
								sources_length - 1));
					}
					displayActivityUserProfileImages(holder, sources);
					break;
				}
				case ParcelableActivity.ACTION_MENTION: {
					if (target_objects != null && target_objects.length > 0) {
						final ParcelableStatus status = target_objects[0];
						displayStatus(status, holder, position);
					}
					break;
				}
				case ParcelableActivity.ACTION_REPLY: {
					if (target_statuses_length > 0) {
						final ParcelableStatus status = target_statuses[0];
						displayStatus(status, holder, position);
					}
					break;
				}
				case ParcelableActivity.ACTION_RETWEET: {

					holder.name.setVisibility(View.VISIBLE);
					holder.screen_name.setVisibility(View.GONE);
					holder.profile_image.setVisibility(View.GONE);
					holder.my_profile_image.setVisibility(View.GONE);
					holder.text.setVisibility(View.VISIBLE);
					holder.reply_retweet_status.setVisibility(View.GONE);

					holder.name.setSingleLine(false);

					if (target_objects != null && target_objects.length > 0) {
						final ParcelableStatus status = target_objects[0];
						if (highlightOption != LINK_HIGHLIGHT_OPTION_CODE_NONE) {
							holder.text.setText(Html.fromHtml(status.text_html));
							linkify.applyAllLinks(holder.text, status.account_id, status.is_possibly_sensitive);
							holder.text.setMovementMethod(null);
						} else {
							holder.text.setText(status.text_unescaped);
						}
					}

					if (sources_length == 1) {
						holder.name.setText(context.getString(R.string.activity_about_me_retweet, sourceName));
					} else {
						holder.name.setText(context.getString(R.string.activity_about_me_retweet_multi, sourceName,
								sources_length - 1));
					}
					holder.activity_profile_images_container.setVisibility(View.VISIBLE);
					displayActivityUserProfileImages(holder, sources);
					break;
				}
				case ParcelableActivity.ACTION_LIST_MEMBER_ADDED: {
					holder.name.setVisibility(View.VISIBLE);
					holder.screen_name.setVisibility(View.GONE);
					holder.profile_image.setVisibility(View.GONE);
					holder.my_profile_image.setVisibility(View.GONE);
					holder.text.setVisibility(View.GONE);
					holder.reply_retweet_status.setVisibility(View.GONE);

					holder.name.setSingleLine(false);

					if (sources_length == 1) {
						if (item.target_object_user_lists != null && item.target_object_user_lists.length > 0) {
							final ParcelableUserList list = item.target_object_user_lists[0];
							holder.name.setText(context.getString(
									R.string.activity_about_me_list_member_added_with_name, sourceName, list.name));
						} else {
							holder.name.setText(context.getString(R.string.activity_about_me_list_member_added,
									sourceName));
						}
					} else {
						holder.name.setText(context.getString(R.string.activity_about_me_list_member_added_multi,
								sourceName, sources_length - 1));
					}
					displayActivityUserProfileImages(holder, sources);
					break;
				}
			}
		}
	}

	private void displayStatus(final ParcelableStatus status, final ActivityViewHolder holder, final int position) {

		final boolean showGap = status.is_gap && !mGapDisallowed && position != getCount() - 1;
		final boolean displayProfileImage = isDisplayProfileImage();

		holder.setShowAsGap(showGap);
		holder.name.setVisibility(View.VISIBLE);
		holder.screen_name.setVisibility(View.VISIBLE);
		holder.text.setVisibility(View.VISIBLE);
		holder.profile_image.setVisibility(displayProfileImage ? View.VISIBLE : View.GONE);
		holder.activity_profile_images_container.setVisibility(View.GONE);

		holder.name.setSingleLine(true);
		holder.text.setSingleLine(false);

		if (!showGap) {
			final TwidereLinkify linkify = getLinkify();
			final int highlightOption = getLinkHighlightOption();
			final boolean showAccountColor = isShowAccountColor();

			// Clear images in prder to prevent images in recycled view shown.
			holder.profile_image.setImageDrawable(null);
			holder.my_profile_image.setImageDrawable(null);
			holder.image_preview.setImageDrawable(null);

			holder.setAccountColorEnabled(showAccountColor);

			if (highlightOption != LINK_HIGHLIGHT_OPTION_CODE_NONE) {
				holder.text.setText(Html.fromHtml(status.text_html));
				linkify.applyAllLinks(holder.text, status.account_id, status.is_possibly_sensitive);
				holder.text.setMovementMethod(null);
			} else {
				holder.text.setText(status.text_unescaped);
			}

			if (showAccountColor) {
				holder.setAccountColor(getAccountColor(mContext, status.account_id));
			}

			final boolean isMyStatus = status.account_id == status.user_id;
			holder.setUserColor(getUserColor(mContext, status.user_id));
			holder.setHighlightColor(getStatusBackground(false, !mFavoritesHighlightDisabled && status.is_favorite,
					status.is_retweet));
			holder.setTextSize(getTextSize());

			holder.setIsMyStatus(isMyStatus && !mIndicateMyStatusDisabled);

			holder.setUserType(status.user_is_verified, status.user_is_protected);
			holder.setDisplayNameFirst(isDisplayNameFirst());
			holder.setNicknameOnly(isNicknameOnly());
			final String nick = getUserNickname(mContext, status.user_id);
			holder.name.setText(TextUtils.isEmpty(nick) ? status.user_name : isNicknameOnly() ? nick : mContext
					.getString(R.string.name_with_nickname, status.user_name, nick));
			holder.screen_name.setText("@" + status.user_screen_name);
			if (highlightOption != LINK_HIGHLIGHT_OPTION_CODE_NONE) {
				linkify.applyUserProfileLinkNoHighlight(holder.name, status.account_id, status.user_id,
						status.user_screen_name);
				linkify.applyUserProfileLinkNoHighlight(holder.screen_name, status.account_id, status.user_id,
						status.user_screen_name);
				holder.name.setMovementMethod(null);
				holder.screen_name.setMovementMethod(null);
			}
			holder.time.setTime(status.timestamp);
			holder.setStatusType(!mFavoritesHighlightDisabled && status.is_favorite,
					ParcelableLocation.isValidLocation(status.location), status.has_media, status.is_possibly_sensitive);
			holder.setIsReplyRetweet(status.in_reply_to_status_id > 0, status.is_retweet);
			if (status.is_retweet) {
				holder.setRetweetedBy(status.retweet_count, status.retweeted_by_id, status.retweeted_by_name,
						status.retweeted_by_screen_name);
			} else if (status.in_reply_to_status_id > 0) {
				holder.setReplyTo(status.in_reply_to_user_id, status.in_reply_to_name, status.in_reply_to_screen_name);
			}
			if (displayProfileImage) {
				setProfileImage(holder.my_profile_image, status);
				setProfileImage(holder.profile_image, status);
				holder.profile_image.setTag(position);
				holder.my_profile_image.setTag(position);
			} else {
				holder.profile_image.setVisibility(View.GONE);
				holder.my_profile_image.setVisibility(View.GONE);
			}
			final boolean hasPreview = mDisplayImagePreview && status.has_media && status.media_link != null;
			holder.image_preview_container.setVisibility(hasPreview ? View.VISIBLE : View.GONE);
			if (hasPreview) {
				if (status.is_possibly_sensitive && !mDisplaySensitiveContents) {
					holder.image_preview.setImageDrawable(null);
					holder.image_preview.setBackgroundResource(R.drawable.image_preview_nsfw);
					holder.image_preview_progress.setVisibility(View.GONE);
				} else if (!status.media_link.equals(mImageLoadingHandler.getLoadingUri(holder.image_preview))) {
					holder.image_preview.setBackgroundResource(0);
					final ImageLoaderWrapper imageLoader = getImageLoader();
					imageLoader.displayPreviewImage(holder.image_preview, status.media_link, mImageLoadingHandler);
				}
				holder.image_preview.setTag(position);
			}
			holder.item_menu.setTag(position);
		}
	}

}
