package model;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.themes.ValoTheme;

public class Styles {

  public static final String textStyle = ValoTheme.TEXTFIELD_SMALL;
  public static final String boxStyle = ValoTheme.COMBOBOX_SMALL;
  public static final String tableStyle = ValoTheme.TABLE_SMALL;

  public enum NotificationType {
    ERROR, SUCCESS, DEFAULT
  }

  public static void notification(String title, String description, NotificationType type) {
    Notification notify = new Notification(title, description);
    notify.setPosition(Position.TOP_CENTER);
    switch (type) {
      case ERROR:
        notify.setDelayMsec(16000);
        notify.setIcon(FontAwesome.FROWN_O);
        notify.setStyleName(ValoTheme.NOTIFICATION_ERROR + " " + ValoTheme.NOTIFICATION_CLOSABLE);
        break;
      case SUCCESS:
        notify.setDelayMsec(8000);
        notify.setIcon(FontAwesome.SMILE_O);
        notify.setStyleName(ValoTheme.NOTIFICATION_SUCCESS + " " + ValoTheme.NOTIFICATION_CLOSABLE);
        break;
      default:
        notify.setDelayMsec(8000);
        notify.setIcon(FontAwesome.COMMENT);
        notify.setStyleName(ValoTheme.NOTIFICATION_TRAY + " " + ValoTheme.NOTIFICATION_CLOSABLE);
        break;
    }
    notify.show(Page.getCurrent());
  }
  
  public static HorizontalLayout questionize(Component c, final String info, final String header) {
    final HorizontalLayout res = new HorizontalLayout();
    res.setSpacing(true);

    res.setVisible(c.isVisible());
    res.setCaption(c.getCaption());
    c.setCaption(null);
    res.addComponent(c);

    PopupView pv = new PopupView(new Content() {

      @Override
      public Component getPopupComponent() {
        Label l = new Label(info, ContentMode.HTML);
        l.setCaption(header);
        l.setIcon(FontAwesome.INFO);
        l.setWidth("350px");
        l.addStyleName("info");
        return new VerticalLayout(l);
      }

      @Override
      public String getMinimizedValueAsHTML() {
        return "[?]";
      }
    });
    pv.setHideOnMouseOut(false);

    res.addComponent(pv);

    return res;
  }

}
