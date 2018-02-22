/*******************************************************************************
 * ''' * QBiC User DB Tools enables users to add people and affiliations to our mysql user database.
 * Copyright (C) 2016 Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package views;

import java.io.File;
import java.io.IOException;

import logging.Log4j2Logger;
import model.Styles;
import model.Styles.NotificationType;

import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;

import helpers.SQLBatchParser;
import helpers.Uploader;

public class BatchUpload extends VerticalLayout {

  private final Uploader uploader = new Uploader();

  logging.Logger logger = new Log4j2Logger(BatchUpload.class);
  private Button register;
  private Upload upload;

  public BatchUpload() {
    setMargin(true);
    setSpacing(true);

    // file upload component
    Upload upload = new Upload("Upload your file here", uploader);
    addComponent(this.upload);
    upload.setEnabled(false);

    // sample registration button
    register = new Button("Register People");
    register.setVisible(false);
    addComponent(register);

    upload.setButtonCaption("Upload");
    // Listen for events regarding the success of upload.
    upload.addFailedListener(uploader);
    upload.addSucceededListener(uploader);
    FinishedListener uploadFinListener = new FinishedListener() {
      /**
       * 
       */
      private static final long serialVersionUID = -8413963075202260180L;

      public void uploadFinished(FinishedEvent event) {
        String uploadError = uploader.getError();
        File file = uploader.getFile();
        if (file.getPath().endsWith("up_")) {
          String msg = "No file selected.";
          logger.warn(msg);
          Styles.notification("Failed to read file.", msg, NotificationType.ERROR);
          if (!file.delete())
            logger.error("uploaded tmp file " + file.getAbsolutePath() + " could not be deleted!");
        } else {
          if (uploadError == null || uploadError.isEmpty()) {
            String msg = "Upload successful!";
            logger.info(msg);
//            try {
              setRegEnabled(false);
              SQLBatchParser parser = new SQLBatchParser();
              if (parser.processTSV()) {
                // TODO = prep.getObjects();
                Styles.notification("Upload successful",
                    "New people information successfully uploaded and read.",
                    NotificationType.SUCCESS);
              } else {
                String error = parser.getError();
                Styles.notification("Failed to read file.", error, NotificationType.ERROR);
                if (!file.delete())
                  logger.error(
                      "uploaded tmp file " + file.getAbsolutePath() + " could not be deleted!");
              }
//            } catch (IOException e) {
//              e.printStackTrace();
//            }
          } else {
            Styles.notification("Failed to upload file.", uploadError, NotificationType.ERROR);
            if (!file.delete())
              logger
                  .error("uploaded tmp file " + file.getAbsolutePath() + " could not be deleted!");
          }
        }
      }
    };
    upload.addFinishedListener(uploadFinListener);
    // view.initUpload(upload);

    Button.ClickListener cl = new Button.ClickListener() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      /**
       * 
       */

      @Override
      public void buttonClick(ClickEvent event) {
        String src = event.getButton().getCaption();
        if (src.equals("Register People")) {
          register.setEnabled(false);
        }
      }
    };
    register.addClickListener(cl);
  }

  public Button getRegisterButton() {
    return this.register;
  }

  public void setRegEnabled(boolean b) {
    register.setEnabled(b);
    register.setVisible(b);
  }

}
