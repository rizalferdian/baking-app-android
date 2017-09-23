package com.example.cendana2000.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link RecipeWidgetConfigureActivity RecipeWidgetConfigureActivity}
 */
public class RecipeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetTitle = RecipeWidgetConfigureActivity.loadWidgetPref(context, appWidgetId, RecipeWidgetConfigureActivity.PREFS_WIDGET_TITLE);
        CharSequence widgetText = RecipeWidgetConfigureActivity.loadWidgetPref(context, appWidgetId, RecipeWidgetConfigureActivity.PREFS_WIDGET_TEXT);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        views.setTextViewText(R.id.recipe_widget_title, widgetTitle);
        views.setTextViewText(R.id.recipe_widget_ingredient, widgetText);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            RecipeWidgetConfigureActivity.deleteWidgetPref(context, appWidgetId, RecipeWidgetConfigureActivity.PREFS_WIDGET_TITLE);
            RecipeWidgetConfigureActivity.deleteWidgetPref(context, appWidgetId, RecipeWidgetConfigureActivity.PREFS_WIDGET_TEXT);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

