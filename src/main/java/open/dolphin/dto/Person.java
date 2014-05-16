/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package open.dolphin.dto;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Person
 *
 * @author manabu nishimura <nishimurama@sandi.co.jp>, S&I Co.,Ltd.
 */
public class Person {

    private BooleanProperty invited;
    private StringProperty id;
    private StringProperty name;
    private StringProperty nameKana;
    private StringProperty sex;
    private StringProperty birthday;
    private StringProperty visitDay;
    private long patientId;

    public Person(boolean invited, String id, String name, String nameKana, String sex, String birthday, String visitDay, long patientId) {
        this.invited = new SimpleBooleanProperty(invited);
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.nameKana = new SimpleStringProperty(nameKana);
        this.sex = new SimpleStringProperty(sex);
        this.birthday = new SimpleStringProperty(birthday);
        this.visitDay = new SimpleStringProperty(visitDay);
        this.invited.addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
//                System.out.println(nameProperty().get() + " invited: " + t1);
            }
        });
        this.patientId = patientId;
    }

    public BooleanProperty invitedProperty() {
        return invited;
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty nameKanaProperty() {
        return nameKana;
    }

    public StringProperty sexProperty() {
        return sex;
    }

    public StringProperty birthdayProperty() {
        return birthday;
    }

    public StringProperty visitDayProperty() {
        return visitDay;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setNameKana(String nameKana) {
        this.nameKana.set(nameKana);
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public void setBirthday(String birthday) {
        this.birthday.set(birthday);
    }

    public void setVisitDay(String visitDay) {
        this.visitDay.set(visitDay);
    }

    public long getPatientId() {
        return patientId;
    }
}
