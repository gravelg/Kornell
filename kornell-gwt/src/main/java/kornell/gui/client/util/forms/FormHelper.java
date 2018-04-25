package kornell.gui.client.util.forms;

import static kornell.core.util.StringUtils.mkurl;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.Device;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

import kornell.gui.client.KornellConstants;
import kornell.gui.client.KornellMessages;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.forms.formfield.CheckBoxFormField;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.forms.formfield.PasswordTextBoxFormField;
import kornell.gui.client.util.forms.formfield.TextAreaFormField;
import kornell.gui.client.util.forms.formfield.TextBoxFormField;

@SuppressWarnings("deprecation")
public class FormHelper {
    public static String SEPARATOR_BAR_IMG_PATH = mkurl(ClientConstants.IMAGES_PATH, "profile", "separatorBar.png");
    public static String SEPARATOR_BAR_CLASS = "profileSeparatorBar";

    public static Character USERNAME_SEPARATOR = '/';
    public static Character USERNAME_ALTERNATE_SEPARATOR = '\\';

    private KornellConstants constants = GWT.create(KornellConstants.class);
    private KornellMessages messages = GWT.create(KornellMessages.class);

    private static final String EMAIL_PATTERN = "^[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,10}$";
    private static final String USERNAME_PATTERN = "^[A-z0-9._]{3,}$";
    private static final String PASSWORD_PATTERN = "^[0-9a-zA-Z!@#$%¨&*()]{6,}$";

    public static boolean isEmailValid(String field) {
        return field == null ? false : field.trim().matches(EMAIL_PATTERN);
    }

    public static boolean isUsernameValid(String field) {
        return field == null ? false : field.trim().matches(USERNAME_PATTERN);
    }

    public boolean isPasswordValid(String field) {
        return field == null ? false : field.trim().matches(PASSWORD_PATTERN);
    }

    public boolean isValidNumber(String field) {
        return field == null ? false : field.trim().matches("^[-+]?[0-9]*\\.?[0-9]+$");
    }

    public boolean isValidInteger(String field) {
        return field == null ? false : field.trim().matches("[0-9]*");
    }

    public boolean isLengthValid(String field, int minLength, int maxLength) {
        return field == null ? false : field.trim().length() >= minLength && field.trim().length() <= maxLength;
    }

    public boolean isNumberRangeValid(Integer field, int min, int max) {
        return field == null ? false : field >= min && field <= max;
    }

    public boolean isLengthValid(String field, int minLength) {
        return isLengthValid(field, minLength, Integer.MAX_VALUE);
    }

    public ListBox getSexList() {
        ListBox sex = new ListBox();

        sex.addItem(constants.selectboxDefault(), "-");
        sex.addItem(constants.genderFemale(), "F");
        sex.addItem(constants.genderMale(), "M");

        return sex;
    }

    public boolean isListBoxSelected(ListBox value) {
        return value != null && !("-".equals(value.getValue()) || value.getValue() == null);
    }

    public TextBoxFormField createTextBoxFormField(String text, String textBoxFormFieldType) {
        TextBox fieldTextBox = new TextBox();
        fieldTextBox.addStyleName("field");
        fieldTextBox.addStyleName("textField");
        fieldTextBox.setValue(text);
        return new TextBoxFormField(fieldTextBox, textBoxFormFieldType);
    }

    public TextBoxFormField createTextBoxFormField(String text) {
        return createTextBoxFormField(text, null);
    }

    public TextAreaFormField createTextAreaFormField(String text, int visibleLines) {
        TextArea fieldTextArea = new TextArea();
        if (visibleLines > 0) {
            fieldTextArea.setVisibleLines(visibleLines);
        }
        fieldTextArea.addStyleName("field");
        fieldTextArea.addStyleName("textField");
        fieldTextArea.setValue(text);
        return new TextAreaFormField(fieldTextArea);
    }

    public TextAreaFormField createTextAreaFormField(String text) {
        return createTextAreaFormField(text, 0);
    }

    public PasswordTextBoxFormField createPasswordTextBoxFormField(String text) {
        PasswordTextBox fieldPasswordTextBox = new PasswordTextBox();
        fieldPasswordTextBox.addStyleName("field");
        fieldPasswordTextBox.addStyleName("textField");
        fieldPasswordTextBox.setValue(text);
        return new PasswordTextBoxFormField(fieldPasswordTextBox);
    }

    public CheckBoxFormField createCheckBoxFormField(Boolean value) {
        CheckBox fieldCheckBox = new CheckBox();
        fieldCheckBox.setValue(value);
        return new CheckBoxFormField(fieldCheckBox);
    }

    public void clearErrors(List<KornellFormFieldWrapper> fields) {
        for (KornellFormFieldWrapper field : fields) {
            field.clearError();
        }
    }

    public boolean checkErrors(List<KornellFormFieldWrapper> fields) {
        for (KornellFormFieldWrapper field : fields) {
            if (!"".equals(field.getError())) {
                return true;
            }
        }
        return false;
    }

    public static String stripCPF(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]+", "");
    }

    public boolean isItemInListBox(String item, ListBox listBox) {
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (item.equals(listBox.getItemText(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCPFValid(String cpf) {
        if (cpf == null) {
            return false;
        }
        cpf = stripCPF(cpf);
        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222")
                || cpf.equals("33333333333") || cpf.equals("44444444444") || cpf.equals("55555555555")
                || cpf.equals("66666666666") || cpf.equals("77777777777") || cpf.equals("88888888888")
                || cpf.equals("99999999999") || (cpf.length() != 11)) {
            return false;
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = cpf.charAt(i) - 48;
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            }
            else {
                dig10 = (char) (r + 48); // converte no respectivo caractere
                // numerico
            }

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = cpf.charAt(i) - 48;
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            // Verifica se os digitos calculados conferem com os digitos
            // informados.
            if ((dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception erro) {
            return false;
        }
    }

    public Image getImageSeparator() {
        Image image = new Image(SEPARATOR_BAR_IMG_PATH);
        image.addStyleName(SEPARATOR_BAR_CLASS);
        return image;
    }

    public String formatCPF(String cpf) {
        cpf = stripCPF(cpf);
        if (cpf == null || cpf.length() != 11) {
            return "";
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    public static void hideTab(Tab tab) {
        tab.setHideOn(Device.PHONE);
    }

    public static void createIcon(Button btn, String iconClass) {
        Icon icon = new Icon();
        icon.addStyleName("fa " + iconClass);
        btn.clear();
        btn.add(icon);
    }

    public String getElapsedTimeSince(Date date, Date now) {
        long dateM = date.getTime() - 1000;
        long serverNowM = now.getTime();

        long diffM = serverNowM - dateM;

        long days = diffM / (24 * 60 * 60 * 1000);
        long hours = diffM / (60 * 60 * 1000);
        long minutes = diffM / (60 * 1000);
        long seconds = diffM / (1000);
        seconds = seconds > 0 ? seconds : 1;

        if (days > 0) {
            return dateToString(date);
        } else if (hours > 0) {
            return messages.hoursAgo(hours, hours > 1);
        } else if (minutes > 0) {
            return messages.minutesAgo(minutes, minutes > 1);
        } else {
            return messages.secondsAgo(seconds, seconds > 1);
        }
    }

    public String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        String month = ((date.getMonth() + 1) < 10 ? "0" : "") + (date.getMonth() + 1);
        String day = (date.getDate() < 10 ? "0" : "") + date.getDate();
        return (1900 + date.getYear()) + "-" + month + "-" + day;

    }

    // No i18n needed for now, this list will only be shown on pt_BR locales
    public ListBox getBrazilianStatesList() {
        ListBox states = new ListBox();

        states.addItem("Selecione:", "-");
        states.addItem("Acre");
        states.addItem("Alagoas");
        states.addItem("Amapá");
        states.addItem("Amazonas");
        states.addItem("Bahia");
        states.addItem("Ceará");
        states.addItem("Distrito Federal");
        states.addItem("Espírito Santo");
        states.addItem("Goiás");
        states.addItem("Maranhão");
        states.addItem("Mato Grosso");
        states.addItem("Mato Grosso do Sul");
        states.addItem("Minas Gerais");
        states.addItem("Pará");
        states.addItem("Paraíba");
        states.addItem("Paraná");
        states.addItem("Pernambuco");
        states.addItem("Piauí");
        states.addItem("Rio de Janeiro");
        states.addItem("Rio Grande do Norte");
        states.addItem("Rio Grande do Sul");
        states.addItem("Rondônia");
        states.addItem("Roraima");
        states.addItem("Santa Catarina");
        states.addItem("São Paulo");
        states.addItem("Sergipe");
        states.addItem("Tocantins");

        return states;
    }

    public ListBox getSkinsList() {
        ListBox skins = new ListBox();

        skins.addItem("Escuro - Padrão", "");
        skins.addItem("Escuro - Verde", "_green");
        skins.addItem("Escuro - Azul", "_blue");
        skins.addItem("Escuro - Amarelo", "_yellow");
        skins.addItem("Escuro - Laranja", "_orange");
        skins.addItem("Escuro - Vermelho", "_red");
        skins.addItem("Claro - Cinza", "_light");
        skins.addItem("Claro - Verde", "_light_green");
        skins.addItem("Claro - Azul", "_light_blue");
        skins.addItem("Claro - Vermelho", "_light_red");

        return skins;
    }

    public ListBox getTimeZonesList() {
        ListBox timeZones = new ListBox();

        timeZones.addItem("Selecione:", "-");
        timeZones.addItem("GMT-12:00 - Etc/GMT+12", "Etc/GMT+12");
        timeZones.addItem("GMT-11:00 - Etc/GMT+11", "Etc/GMT+11");
        timeZones.addItem("GMT-11:00 - Pacific/Apia", "Pacific/Apia");
        timeZones.addItem("GMT-11:00 - Pacific/Midway", "Pacific/Midway");
        timeZones.addItem("GMT-11:00 - Pacific/Niue", "Pacific/Niue");
        timeZones.addItem("GMT-11:00 - Pacific/Pago_Pago", "Pacific/Pago_Pago");
        timeZones.addItem("GMT-10:00 - America/Adak", "America/Adak");
        timeZones.addItem("GMT-10:00 - Etc/GMT+10", "Etc/GMT+10");
        timeZones.addItem("GMT-10:00 - HST", "HST");
        timeZones.addItem("GMT-10:00 - Pacific/Fakaofo", "Pacific/Fakaofo");
        timeZones.addItem("GMT-10:00 - Pacific/Honolulu", "Pacific/Honolulu");
        timeZones.addItem("GMT-10:00 - Pacific/Johnston", "Pacific/Johnston");
        timeZones.addItem("GMT-10:00 - Pacific/Rarotonga", "Pacific/Rarotonga");
        timeZones.addItem("GMT-10:00 - Pacific/Tahiti", "Pacific/Tahiti");
        timeZones.addItem("GMT0 - Pacific/Marquesas", "Pacific/Marquesas");
        timeZones.addItem("GMT-09:00 - America/Anchorage", "America/Anchorage");
        timeZones.addItem("GMT-09:00 - America/Juneau", "America/Juneau");
        timeZones.addItem("GMT-09:00 - America/Nome", "America/Nome");
        timeZones.addItem("GMT-09:00 - America/Yakutat", "America/Yakutat");
        timeZones.addItem("GMT-09:00 - Etc/GMT+9", "Etc/GMT+9");
        timeZones.addItem("GMT-09:00 - Pacific/Gambier", "Pacific/Gambier");
        timeZones.addItem("GMT-08:00 - America/Dawson", "America/Dawson");
        timeZones.addItem("GMT-08:00 - America/Los_Angeles", "America/Los_Angeles");
        timeZones.addItem("GMT-08:00 - America/Santa_Isabel", "America/Santa_Isabel");
        timeZones.addItem("GMT-08:00 - America/Tijuana", "America/Tijuana");
        timeZones.addItem("GMT-08:00 - America/Vancouver", "America/Vancouver");
        timeZones.addItem("GMT-08:00 - America/Whitehorse", "America/Whitehorse");
        timeZones.addItem("GMT-08:00 - Etc/GMT+8", "Etc/GMT+8");
        timeZones.addItem("GMT-08:00 - PST8PDT", "PST8PDT");
        timeZones.addItem("GMT-08:00 - Pacific/Pitcairn", "Pacific/Pitcairn");
        timeZones.addItem("GMT-07:00 - America/Boise", "America/Boise");
        timeZones.addItem("GMT-07:00 - America/Cambridge_Bay", "America/Cambridge_Bay");
        timeZones.addItem("GMT-07:00 - America/Chihuahua", "America/Chihuahua");
        timeZones.addItem("GMT-07:00 - America/Dawson_Creek", "America/Dawson_Creek");
        timeZones.addItem("GMT-07:00 - America/Denver", "America/Denver");
        timeZones.addItem("GMT-07:00 - America/Edmonton", "America/Edmonton");
        timeZones.addItem("GMT-07:00 - America/Hermosillo", "America/Hermosillo");
        timeZones.addItem("GMT-07:00 - America/Inuvik", "America/Inuvik");
        timeZones.addItem("GMT-07:00 - America/Mazatlan", "America/Mazatlan");
        timeZones.addItem("GMT-07:00 - America/Ojinaga", "America/Ojinaga");
        timeZones.addItem("GMT-07:00 - America/Phoenix", "America/Phoenix");
        timeZones.addItem("GMT-07:00 - America/Yellowknife", "America/Yellowknife");
        timeZones.addItem("GMT-07:00 - Etc/GMT+7", "Etc/GMT+7");
        timeZones.addItem("GMT-07:00 - MST", "MST");
        timeZones.addItem("GMT-07:00 - MST7MDT", "MST7MDT");
        timeZones.addItem("GMT-06:00 - America/Bahia_Banderas", "America/Bahia_Banderas");
        timeZones.addItem("GMT-06:00 - America/Belize", "America/Belize");
        timeZones.addItem("GMT-06:00 - America/Cancun", "America/Cancun");
        timeZones.addItem("GMT-06:00 - America/Chicago", "America/Chicago");
        timeZones.addItem("GMT-06:00 - America/Costa_Rica", "America/Costa_Rica");
        timeZones.addItem("GMT-06:00 - America/El_Salvador", "America/El_Salvador");
        timeZones.addItem("GMT-06:00 - America/Guatemala", "America/Guatemala");
        timeZones.addItem("GMT-06:00 - America/Indiana/Knox", "America/Indiana/Knox");
        timeZones.addItem("GMT-06:00 - America/Indiana/Tell_City", "America/Indiana/Tell_City");
        timeZones.addItem("GMT-06:00 - America/Managua", "America/Managua");
        timeZones.addItem("GMT-06:00 - America/Matamoros", "America/Matamoros");
        timeZones.addItem("GMT-06:00 - America/Menominee", "America/Menominee");
        timeZones.addItem("GMT-06:00 - America/Merida", "America/Merida");
        timeZones.addItem("GMT-06:00 - America/Mexico_City", "America/Mexico_City");
        timeZones.addItem("GMT-06:00 - America/Monterrey", "America/Monterrey");
        timeZones.addItem("GMT-06:00 - America/North_Dakota/Center", "America/North_Dakota/Center");
        timeZones.addItem("GMT-06:00 - America/North_Dakota/New_Salem", "America/North_Dakota/New_Salem");
        timeZones.addItem("GMT-06:00 - America/Rainy_River", "America/Rainy_River");
        timeZones.addItem("GMT-06:00 - America/Rankin_Inlet", "America/Rankin_Inlet");
        timeZones.addItem("GMT-06:00 - America/Regina", "America/Regina");
        timeZones.addItem("GMT-06:00 - America/Swift_Current", "America/Swift_Current");
        timeZones.addItem("GMT-06:00 - America/Tegucigalpa", "America/Tegucigalpa");
        timeZones.addItem("GMT-06:00 - America/Winnipeg", "America/Winnipeg");
        timeZones.addItem("GMT-06:00 - CST6CDT", "CST6CDT");
        timeZones.addItem("GMT-06:00 - Etc/GMT+6", "Etc/GMT+6");
        timeZones.addItem("GMT-06:00 - Pacific/Easter", "Pacific/Easter");
        timeZones.addItem("GMT-06:00 - Pacific/Galapagos", "Pacific/Galapagos");
        timeZones.addItem("GMT-05:00 - America/Atikokan", "America/Atikokan");
        timeZones.addItem("GMT-05:00 - America/Bogota", "America/Bogota");
        timeZones.addItem("GMT-05:00 - America/Cayman", "America/Cayman");
        timeZones.addItem("GMT-05:00 - America/Detroit", "America/Detroit");
        timeZones.addItem("GMT-05:00 - America/Grand_Turk", "America/Grand_Turk");
        timeZones.addItem("GMT-05:00 - America/Guayaquil", "America/Guayaquil");
        timeZones.addItem("GMT-05:00 - America/Havana", "America/Havana");
        timeZones.addItem("GMT-05:00 - America/Indiana/Indianapolis", "America/Indiana/Indianapolis");
        timeZones.addItem("GMT-05:00 - America/Indiana/Marengo", "America/Indiana/Marengo");
        timeZones.addItem("GMT-05:00 - America/Indiana/Petersburg", "America/Indiana/Petersburg");
        timeZones.addItem("GMT-05:00 - America/Indiana/Vevay", "America/Indiana/Vevay");
        timeZones.addItem("GMT-05:00 - America/Indiana/Vincennes", "America/Indiana/Vincennes");
        timeZones.addItem("GMT-05:00 - America/Indiana/Winamac", "America/Indiana/Winamac");
        timeZones.addItem("GMT-05:00 - America/Iqaluit", "America/Iqaluit");
        timeZones.addItem("GMT-05:00 - America/Jamaica", "America/Jamaica");
        timeZones.addItem("GMT-05:00 - America/Kentucky/Louisville", "America/Kentucky/Louisville");
        timeZones.addItem("GMT-05:00 - America/Kentucky/Monticello", "America/Kentucky/Monticello");
        timeZones.addItem("GMT-05:00 - America/Lima", "America/Lima");
        timeZones.addItem("GMT-05:00 - America/Montreal", "America/Montreal");
        timeZones.addItem("GMT-05:00 - America/Nassau", "America/Nassau");
        timeZones.addItem("GMT-05:00 - America/New_York", "America/New_York");
        timeZones.addItem("GMT-05:00 - America/Nipigon", "America/Nipigon");
        timeZones.addItem("GMT-05:00 - America/Panama", "America/Panama");
        timeZones.addItem("GMT-05:00 - America/Pangnirtung", "America/Pangnirtung");
        timeZones.addItem("GMT-05:00 - America/Port-au-Prince", "America/Port-au-Prince");
        timeZones.addItem("GMT-05:00 - America/Resolute", "America/Resolute");
        timeZones.addItem("GMT-05:00 - America/Thunder_Bay", "America/Thunder_Bay");
        timeZones.addItem("GMT-05:00 - America/Toronto", "America/Toronto");
        timeZones.addItem("GMT-05:00 - EST", "EST");
        timeZones.addItem("GMT-05:00 - EST5EDT", "EST5EDT");
        timeZones.addItem("GMT-05:00 - Etc/GMT+5", "Etc/GMT+5");
        timeZones.addItem("GMT-04:30 - America/Caracas", "America/Caracas");
        timeZones.addItem("GMT-04:00 - America/Anguilla", "America/Anguilla");
        timeZones.addItem("GMT-04:00 - America/Antigua", "America/Antigua");
        timeZones.addItem("GMT-03:00 - America/Argentina/San_Luis", "America/Argentina/San_Luis");
        timeZones.addItem("GMT-04:00 - America/Aruba", "America/Aruba");
        timeZones.addItem("GMT-04:00 - America/Asuncion", "America/Asuncion");
        timeZones.addItem("GMT-04:00 - America/Barbados", "America/Barbados");
        timeZones.addItem("GMT-04:00 - America/Blanc-Sablon", "America/Blanc-Sablon");
        timeZones.addItem("GMT-04:00 - America/Boa_Vista", "America/Boa_Vista");
        timeZones.addItem("GMT-04:00 - America/Campo_Grande", "America/Campo_Grande");
        timeZones.addItem("GMT-04:00 - America/Cuiaba", "America/Cuiaba");
        timeZones.addItem("GMT-04:00 - America/Curacao", "America/Curacao");
        timeZones.addItem("GMT-04:00 - America/Dominica", "America/Dominica");
        timeZones.addItem("GMT-04:00 - America/Eirunepe", "America/Eirunepe");
        timeZones.addItem("GMT-04:00 - America/Glace_Bay", "America/Glace_Bay");
        timeZones.addItem("GMT-04:00 - America/Goose_Bay", "America/Goose_Bay");
        timeZones.addItem("GMT-04:00 - America/Grenada", "America/Grenada");
        timeZones.addItem("GMT-04:00 - America/Guadeloupe", "America/Guadeloupe");
        timeZones.addItem("GMT-04:00 - America/Guyana", "America/Guyana");
        timeZones.addItem("GMT-04:00 - America/Halifax", "America/Halifax");
        timeZones.addItem("GMT-04:00 - America/La_Paz", "America/La_Paz");
        timeZones.addItem("GMT-04:00 - America/Manaus", "America/Manaus");
        timeZones.addItem("GMT-04:00 - America/Martinique", "America/Martinique");
        timeZones.addItem("GMT-04:00 - America/Moncton", "America/Moncton");
        timeZones.addItem("GMT-04:00 - America/Montserrat", "America/Montserrat");
        timeZones.addItem("GMT-04:00 - America/Port_of_Spain", "America/Port_of_Spain");
        timeZones.addItem("GMT-04:00 - America/Porto_Velho", "America/Porto_Velho");
        timeZones.addItem("GMT-04:00 - America/Puerto_Rico", "America/Puerto_Rico");
        timeZones.addItem("GMT-04:00 - America/Rio_Branco", "America/Rio_Branco");
        timeZones.addItem("GMT-04:00 - America/Santiago", "America/Santiago");
        timeZones.addItem("GMT-04:00 - America/Santo_Domingo", "America/Santo_Domingo");
        timeZones.addItem("GMT-04:00 - America/St_Kitts", "America/St_Kitts");
        timeZones.addItem("GMT-04:00 - America/St_Lucia", "America/St_Lucia");
        timeZones.addItem("GMT-04:00 - America/St_Thomas", "America/St_Thomas");
        timeZones.addItem("GMT-04:00 - America/St_Vincent", "America/St_Vincent");
        timeZones.addItem("GMT-04:00 - America/Thule", "America/Thule");
        timeZones.addItem("GMT-04:00 - America/Tortola", "America/Tortola");
        timeZones.addItem("GMT-04:00 - Antarctica/Palmer", "Antarctica/Palmer");
        timeZones.addItem("GMT-04:00 - Atlantic/Bermuda", "Atlantic/Bermuda");
        timeZones.addItem("GMT-04:00 - Atlantic/Stanley", "Atlantic/Stanley");
        timeZones.addItem("GMT-04:00 - Etc/GMT+4", "Etc/GMT+4");
        timeZones.addItem("GMT-03:30 - America/St_Johns", "America/St_Johns");
        timeZones.addItem("GMT-03:00 - America/Araguaina", "America/Araguaina");
        timeZones.addItem("GMT-03:00 - America/Argentina/Buenos_Aires", "America/Argentina/Buenos_Aires");
        timeZones.addItem("GMT-03:00 - America/Argentina/Catamarca", "America/Argentina/Catamarca");
        timeZones.addItem("GMT-03:00 - America/Argentina/Cordoba", "America/Argentina/Cordoba");
        timeZones.addItem("GMT-03:00 - America/Argentina/Jujuy", "America/Argentina/Jujuy");
        timeZones.addItem("GMT-03:00 - America/Argentina/La_Rioja", "America/Argentina/La_Rioja");
        timeZones.addItem("GMT-03:00 - America/Argentina/Mendoza", "America/Argentina/Mendoza");
        timeZones.addItem("GMT-03:00 - America/Argentina/Rio_Gallegos", "America/Argentina/Rio_Gallegos");
        timeZones.addItem("GMT-03:00 - America/Argentina/Salta", "America/Argentina/Salta");
        timeZones.addItem("GMT-03:00 - America/Argentina/San_Juan", "America/Argentina/San_Juan");
        timeZones.addItem("GMT-03:00 - America/Argentina/Tucuman", "America/Argentina/Tucuman");
        timeZones.addItem("GMT-03:00 - America/Argentina/Ushuaia", "America/Argentina/Ushuaia");
        timeZones.addItem("GMT-03:00 - America/Bahia", "America/Bahia");
        timeZones.addItem("GMT-03:00 - America/Belem", "America/Belem");
        timeZones.addItem("GMT-03:00 - America/Cayenne", "America/Cayenne");
        timeZones.addItem("GMT-03:00 - America/Fortaleza", "America/Fortaleza");
        timeZones.addItem("GMT-03:00 - America/Godthab", "America/Godthab");
        timeZones.addItem("GMT-03:00 - America/Maceio", "America/Maceio");
        timeZones.addItem("GMT-03:00 - America/Miquelon", "America/Miquelon");
        timeZones.addItem("GMT-03:00 - America/Montevideo", "America/Montevideo");
        timeZones.addItem("GMT-03:00 - America/Paramaribo", "America/Paramaribo");
        timeZones.addItem("GMT-03:00 - America/Recife", "America/Recife");
        timeZones.addItem("GMT-03:00 - America/Santarem", "America/Santarem");
        timeZones.addItem("GMT-03:00 - America/Sao_Paulo", "America/Sao_Paulo");
        timeZones.addItem("GMT-03:00 - Antarctica/Rothera", "Antarctica/Rothera");
        timeZones.addItem("GMT-03:00 - Etc/GMT+3", "Etc/GMT+3");
        timeZones.addItem("GMT-02:00 - America/Noronha", "America/Noronha");
        timeZones.addItem("GMT-02:00 - Atlantic/South_Georgia", "Atlantic/South_Georgia");
        timeZones.addItem("GMT-02:00 - Etc/GMT+2", "Etc/GMT+2");
        timeZones.addItem("GMT-01:00 - America/Scoresbysund", "America/Scoresbysund");
        timeZones.addItem("GMT-01:00 - Atlantic/Azores", "Atlantic/Azores");
        timeZones.addItem("GMT-01:00 - Atlantic/Cape_Verde", "Atlantic/Cape_Verde");
        timeZones.addItem("GMT-01:00 - Etc/GMT+1", "Etc/GMT+1");
        timeZones.addItem("GMT+00:00 - Africa/Abidjan", "Africa/Abidjan");
        timeZones.addItem("GMT+00:00 - Africa/Accra", "Africa/Accra");
        timeZones.addItem("GMT+00:00 - Africa/Bamako", "Africa/Bamako");
        timeZones.addItem("GMT+00:00 - Africa/Banjul", "Africa/Banjul");
        timeZones.addItem("GMT+00:00 - Africa/Bissau", "Africa/Bissau");
        timeZones.addItem("GMT+00:00 - Africa/Casablanca", "Africa/Casablanca");
        timeZones.addItem("GMT+00:00 - Africa/Conakry", "Africa/Conakry");
        timeZones.addItem("GMT+00:00 - Africa/Dakar", "Africa/Dakar");
        timeZones.addItem("GMT+00:00 - Africa/El_Aaiun", "Africa/El_Aaiun");
        timeZones.addItem("GMT+00:00 - Africa/Freetown", "Africa/Freetown");
        timeZones.addItem("GMT+00:00 - Africa/Lome", "Africa/Lome");
        timeZones.addItem("GMT+00:00 - Africa/Monrovia", "Africa/Monrovia");
        timeZones.addItem("GMT+00:00 - Africa/Nouakchott", "Africa/Nouakchott");
        timeZones.addItem("GMT+00:00 - Africa/Ouagadougou", "Africa/Ouagadougou");
        timeZones.addItem("GMT+00:00 - Africa/Sao_Tome", "Africa/Sao_Tome");
        timeZones.addItem("GMT+00:00 - America/Danmarkshavn", "America/Danmarkshavn");
        timeZones.addItem("GMT+00:00 - Atlantic/Canary", "Atlantic/Canary");
        timeZones.addItem("GMT+00:00 - Atlantic/Faroe", "Atlantic/Faroe");
        timeZones.addItem("GMT+00:00 - Atlantic/Madeira", "Atlantic/Madeira");
        timeZones.addItem("GMT+00:00 - Atlantic/Reykjavik", "Atlantic/Reykjavik");
        timeZones.addItem("GMT+00:00 - Atlantic/St_Helena", "Atlantic/St_Helena");
        timeZones.addItem("GMT+00:00 - Etc/GMT", "Etc/GMT");
        timeZones.addItem("GMT+00:00 - Etc/UCT", "Etc/UCT");
        timeZones.addItem("GMT+00:00 - Etc/UTC", "Etc/UTC");
        timeZones.addItem("GMT+00:00 - Europe/Dublin", "Europe/Dublin");
        timeZones.addItem("GMT+00:00 - Europe/Lisbon", "Europe/Lisbon");
        timeZones.addItem("GMT+00:00 - Europe/London", "Europe/London");
        timeZones.addItem("GMT+00:00 - UTC", "UTC");
        timeZones.addItem("GMT+00:00 - WET", "WET");
        timeZones.addItem("GMT+01:00 - Africa/Algiers", "Africa/Algiers");
        timeZones.addItem("GMT+01:00 - Africa/Bangui", "Africa/Bangui");
        timeZones.addItem("GMT+01:00 - Africa/Brazzaville", "Africa/Brazzaville");
        timeZones.addItem("GMT+01:00 - Africa/Ceuta", "Africa/Ceuta");
        timeZones.addItem("GMT+01:00 - Africa/Douala", "Africa/Douala");
        timeZones.addItem("GMT+01:00 - Africa/Kinshasa", "Africa/Kinshasa");
        timeZones.addItem("GMT+01:00 - Africa/Lagos", "Africa/Lagos");
        timeZones.addItem("GMT+01:00 - Africa/Libreville", "Africa/Libreville");
        timeZones.addItem("GMT+01:00 - Africa/Luanda", "Africa/Luanda");
        timeZones.addItem("GMT+01:00 - Africa/Malabo", "Africa/Malabo");
        timeZones.addItem("GMT+01:00 - Africa/Ndjamena", "Africa/Ndjamena");
        timeZones.addItem("GMT+01:00 - Africa/Niamey", "Africa/Niamey");
        timeZones.addItem("GMT+01:00 - Africa/Porto-Novo", "Africa/Porto-Novo");
        timeZones.addItem("GMT+01:00 - Africa/Tunis", "Africa/Tunis");
        timeZones.addItem("GMT+01:00 - Africa/Windhoek", "Africa/Windhoek");
        timeZones.addItem("GMT+01:00 - CET", "CET");
        timeZones.addItem("GMT+01:00 - Etc/GMT-1", "Etc/GMT-1");
        timeZones.addItem("GMT+01:00 - Europe/Amsterdam", "Europe/Amsterdam");
        timeZones.addItem("GMT+01:00 - Europe/Andorra", "Europe/Andorra");
        timeZones.addItem("GMT+01:00 - Europe/Belgrade", "Europe/Belgrade");
        timeZones.addItem("GMT+01:00 - Europe/Berlin", "Europe/Berlin");
        timeZones.addItem("GMT+01:00 - Europe/Brussels", "Europe/Brussels");
        timeZones.addItem("GMT+01:00 - Europe/Budapest", "Europe/Budapest");
        timeZones.addItem("GMT+01:00 - Europe/Copenhagen", "Europe/Copenhagen");
        timeZones.addItem("GMT+01:00 - Europe/Gibraltar", "Europe/Gibraltar");
        timeZones.addItem("GMT+01:00 - Europe/Luxembourg", "Europe/Luxembourg");
        timeZones.addItem("GMT+01:00 - Europe/Madrid", "Europe/Madrid");
        timeZones.addItem("GMT+01:00 - Europe/Malta", "Europe/Malta");
        timeZones.addItem("GMT+01:00 - Europe/Monaco", "Europe/Monaco");
        timeZones.addItem("GMT+01:00 - Europe/Oslo", "Europe/Oslo");
        timeZones.addItem("GMT+01:00 - Europe/Paris", "Europe/Paris");
        timeZones.addItem("GMT+01:00 - Europe/Prague", "Europe/Prague");
        timeZones.addItem("GMT+01:00 - Europe/Rome", "Europe/Rome");
        timeZones.addItem("GMT+01:00 - Europe/Stockholm", "Europe/Stockholm");
        timeZones.addItem("GMT+01:00 - Europe/Tirane", "Europe/Tirane");
        timeZones.addItem("GMT+01:00 - Europe/Vaduz", "Europe/Vaduz");
        timeZones.addItem("GMT+01:00 - Europe/Vienna", "Europe/Vienna");
        timeZones.addItem("GMT+01:00 - Europe/Warsaw", "Europe/Warsaw");
        timeZones.addItem("GMT+01:00 - Europe/Zurich", "Europe/Zurich");
        timeZones.addItem("GMT+01:00 - MET", "MET");
        timeZones.addItem("GMT+02:00 - Africa/Blantyre", "Africa/Blantyre");
        timeZones.addItem("GMT+02:00 - Africa/Bujumbura", "Africa/Bujumbura");
        timeZones.addItem("GMT+02:00 - Africa/Cairo", "Africa/Cairo");
        timeZones.addItem("GMT+02:00 - Africa/Gaborone", "Africa/Gaborone");
        timeZones.addItem("GMT+02:00 - Africa/Harare", "Africa/Harare");
        timeZones.addItem("GMT+02:00 - Africa/Johannesburg", "Africa/Johannesburg");
        timeZones.addItem("GMT+02:00 - Africa/Kigali", "Africa/Kigali");
        timeZones.addItem("GMT+02:00 - Africa/Lubumbashi", "Africa/Lubumbashi");
        timeZones.addItem("GMT+02:00 - Africa/Lusaka", "Africa/Lusaka");
        timeZones.addItem("GMT+02:00 - Africa/Maputo", "Africa/Maputo");
        timeZones.addItem("GMT+02:00 - Africa/Maseru", "Africa/Maseru");
        timeZones.addItem("GMT+02:00 - Africa/Mbabane", "Africa/Mbabane");
        timeZones.addItem("GMT+02:00 - Africa/Tripoli", "Africa/Tripoli");
        timeZones.addItem("GMT+02:00 - Asia/Amman", "Asia/Amman");
        timeZones.addItem("GMT+02:00 - Asia/Beirut", "Asia/Beirut");
        timeZones.addItem("GMT+02:00 - Asia/Damascus", "Asia/Damascus");
        timeZones.addItem("GMT+02:00 - Asia/Gaza", "Asia/Gaza");
        timeZones.addItem("GMT+02:00 - Asia/Jerusalem", "Asia/Jerusalem");
        timeZones.addItem("GMT+02:00 - Asia/Nicosia", "Asia/Nicosia");
        timeZones.addItem("GMT+02:00 - EET", "EET");
        timeZones.addItem("GMT+02:00 - Etc/GMT-2", "Etc/GMT-2");
        timeZones.addItem("GMT+02:00 - Europe/Athens", "Europe/Athens");
        timeZones.addItem("GMT+02:00 - Europe/Bucharest", "Europe/Bucharest");
        timeZones.addItem("GMT+02:00 - Europe/Chisinau", "Europe/Chisinau");
        timeZones.addItem("GMT+02:00 - Europe/Helsinki", "Europe/Helsinki");
        timeZones.addItem("GMT+02:00 - Europe/Istanbul", "Europe/Istanbul");
        timeZones.addItem("GMT+02:00 - Europe/Kaliningrad", "Europe/Kaliningrad");
        timeZones.addItem("GMT+02:00 - Europe/Kiev", "Europe/Kiev");
        timeZones.addItem("GMT+02:00 - Europe/Minsk", "Europe/Minsk");
        timeZones.addItem("GMT+02:00 - Europe/Riga", "Europe/Riga");
        timeZones.addItem("GMT+02:00 - Europe/Simferopol", "Europe/Simferopol");
        timeZones.addItem("GMT+02:00 - Europe/Sofia", "Europe/Sofia");
        timeZones.addItem("GMT+02:00 - Europe/Tallinn", "Europe/Tallinn");
        timeZones.addItem("GMT+02:00 - Europe/Uzhgorod", "Europe/Uzhgorod");
        timeZones.addItem("GMT+02:00 - Europe/Vilnius", "Europe/Vilnius");
        timeZones.addItem("GMT+02:00 - Europe/Zaporozhye", "Europe/Zaporozhye");
        timeZones.addItem("GMT+03:00 - Africa/Addis_Ababa", "Africa/Addis_Ababa");
        timeZones.addItem("GMT+03:00 - Africa/Asmara", "Africa/Asmara");
        timeZones.addItem("GMT+03:00 - Africa/Dar_es_Salaam", "Africa/Dar_es_Salaam");
        timeZones.addItem("GMT+03:00 - Africa/Djibouti", "Africa/Djibouti");
        timeZones.addItem("GMT+03:00 - Africa/Kampala", "Africa/Kampala");
        timeZones.addItem("GMT+03:00 - Africa/Khartoum", "Africa/Khartoum");
        timeZones.addItem("GMT+03:00 - Africa/Mogadishu", "Africa/Mogadishu");
        timeZones.addItem("GMT+03:00 - Africa/Nairobi", "Africa/Nairobi");
        timeZones.addItem("GMT+03:00 - Antarctica/Syowa", "Antarctica/Syowa");
        timeZones.addItem("GMT+03:00 - Asia/Aden", "Asia/Aden");
        timeZones.addItem("GMT+03:00 - Asia/Baghdad", "Asia/Baghdad");
        timeZones.addItem("GMT+03:00 - Asia/Bahrain", "Asia/Bahrain");
        timeZones.addItem("GMT+03:00 - Asia/Kuwait", "Asia/Kuwait");
        timeZones.addItem("GMT+03:00 - Asia/Qatar", "Asia/Qatar");
        timeZones.addItem("GMT+03:00 - Asia/Riyadh", "Asia/Riyadh");
        timeZones.addItem("GMT+03:00 - Etc/GMT-3", "Etc/GMT-3");
        timeZones.addItem("GMT+03:00 - Europe/Moscow", "Europe/Moscow");
        timeZones.addItem("GMT+03:00 - Europe/Samara", "Europe/Samara");
        timeZones.addItem("GMT+03:00 - Europe/Volgograd", "Europe/Volgograd");
        timeZones.addItem("GMT+03:00 - Indian/Antananarivo", "Indian/Antananarivo");
        timeZones.addItem("GMT+03:00 - Indian/Comoro", "Indian/Comoro");
        timeZones.addItem("GMT+03:00 - Indian/Mayotte", "Indian/Mayotte");
        timeZones.addItem("GMT+03:30 - Asia/Tehran", "Asia/Tehran");
        timeZones.addItem("GMT+04:00 - Asia/Baku", "Asia/Baku");
        timeZones.addItem("GMT+04:00 - Asia/Dubai", "Asia/Dubai");
        timeZones.addItem("GMT+04:00 - Asia/Muscat", "Asia/Muscat");
        timeZones.addItem("GMT+04:00 - Asia/Tbilisi", "Asia/Tbilisi");
        timeZones.addItem("GMT+04:00 - Asia/Yerevan", "Asia/Yerevan");
        timeZones.addItem("GMT+04:00 - Etc/GMT-4", "Etc/GMT-4");
        timeZones.addItem("GMT+04:00 - Indian/Mahe", "Indian/Mahe");
        timeZones.addItem("GMT+04:00 - Indian/Mauritius", "Indian/Mauritius");
        timeZones.addItem("GMT+04:00 - Indian/Reunion", "Indian/Reunion");
        timeZones.addItem("GMT+04:30 - Asia/Kabul", "Asia/Kabul");
        timeZones.addItem("GMT+05:00 - Antarctica/Mawson", "Antarctica/Mawson");
        timeZones.addItem("GMT+05:00 - Asia/Aqtau", "Asia/Aqtau");
        timeZones.addItem("GMT+05:00 - Asia/Aqtobe", "Asia/Aqtobe");
        timeZones.addItem("GMT+05:00 - Asia/Ashgabat", "Asia/Ashgabat");
        timeZones.addItem("GMT+05:00 - Asia/Dushanbe", "Asia/Dushanbe");
        timeZones.addItem("GMT+05:00 - Asia/Karachi", "Asia/Karachi");
        timeZones.addItem("GMT+05:00 - Asia/Oral", "Asia/Oral");
        timeZones.addItem("GMT+05:00 - Asia/Samarkand", "Asia/Samarkand");
        timeZones.addItem("GMT+05:00 - Asia/Tashkent", "Asia/Tashkent");
        timeZones.addItem("GMT+05:00 - Asia/Yekaterinburg", "Asia/Yekaterinburg");
        timeZones.addItem("GMT+05:00 - Etc/GMT-5", "Etc/GMT-5");
        timeZones.addItem("GMT+05:00 - Indian/Kerguelen", "Indian/Kerguelen");
        timeZones.addItem("GMT+05:00 - Indian/Maldives", "Indian/Maldives");
        timeZones.addItem("GMT+05:30 - Asia/Colombo", "Asia/Colombo");
        timeZones.addItem("GMT+05:30 - Asia/Kolkata", "Asia/Kolkata");
        timeZones.addItem("GMT+05:45 - Asia/Kathmandu", "Asia/Kathmandu");
        timeZones.addItem("GMT+06:00 - Antarctica/Vostok", "Antarctica/Vostok");
        timeZones.addItem("GMT+06:00 - Asia/Almaty", "Asia/Almaty");
        timeZones.addItem("GMT+06:00 - Asia/Bishkek", "Asia/Bishkek");
        timeZones.addItem("GMT+06:00 - Asia/Dhaka", "Asia/Dhaka");
        timeZones.addItem("GMT+06:00 - Asia/Novokuznetsk", "Asia/Novokuznetsk");
        timeZones.addItem("GMT+06:00 - Asia/Novosibirsk", "Asia/Novosibirsk");
        timeZones.addItem("GMT+06:00 - Asia/Omsk", "Asia/Omsk");
        timeZones.addItem("GMT+06:00 - Asia/Qyzylorda", "Asia/Qyzylorda");
        timeZones.addItem("GMT+06:00 - Asia/Thimphu", "Asia/Thimphu");
        timeZones.addItem("GMT+06:00 - Etc/GMT-6", "Etc/GMT-6");
        timeZones.addItem("GMT+06:00 - Indian/Chagos", "Indian/Chagos");
        timeZones.addItem("GMT+06:30 - Asia/Rangoon", "Asia/Rangoon");
        timeZones.addItem("GMT+06:30 - Indian/Cocos", "Indian/Cocos");
        timeZones.addItem("GMT+07:00 - Antarctica/Davis", "Antarctica/Davis");
        timeZones.addItem("GMT+07:00 - Asia/Bangkok", "Asia/Bangkok");
        timeZones.addItem("GMT+07:00 - Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
        timeZones.addItem("GMT+07:00 - Asia/Hovd", "Asia/Hovd");
        timeZones.addItem("GMT+07:00 - Asia/Jakarta", "Asia/Jakarta");
        timeZones.addItem("GMT+07:00 - Asia/Krasnoyarsk", "Asia/Krasnoyarsk");
        timeZones.addItem("GMT+07:00 - Asia/Phnom_Penh", "Asia/Phnom_Penh");
        timeZones.addItem("GMT+07:00 - Asia/Pontianak", "Asia/Pontianak");
        timeZones.addItem("GMT+07:00 - Asia/Vientiane", "Asia/Vientiane");
        timeZones.addItem("GMT+07:00 - Etc/GMT-7", "Etc/GMT-7");
        timeZones.addItem("GMT+07:00 - Indian/Christmas", "Indian/Christmas");
        timeZones.addItem("GMT+08:00 - Antarctica/Casey", "Antarctica/Casey");
        timeZones.addItem("GMT+08:00 - Asia/Brunei", "Asia/Brunei");
        timeZones.addItem("GMT+08:00 - Asia/Choibalsan", "Asia/Choibalsan");
        timeZones.addItem("GMT+08:00 - Asia/Chongqing", "Asia/Chongqing");
        timeZones.addItem("GMT+08:00 - Asia/Harbin", "Asia/Harbin");
        timeZones.addItem("GMT+08:00 - Asia/Hong_Kong", "Asia/Hong_Kong");
        timeZones.addItem("GMT+08:00 - Asia/Irkutsk", "Asia/Irkutsk");
        timeZones.addItem("GMT+08:00 - Asia/Kashgar", "Asia/Kashgar");
        timeZones.addItem("GMT+08:00 - Asia/Kuala_Lumpur", "Asia/Kuala_Lumpur");
        timeZones.addItem("GMT+08:00 - Asia/Kuching", "Asia/Kuching");
        timeZones.addItem("GMT+08:00 - Asia/Macau", "Asia/Macau");
        timeZones.addItem("GMT+08:00 - Asia/Makassar", "Asia/Makassar");
        timeZones.addItem("GMT+08:00 - Asia/Manila", "Asia/Manila");
        timeZones.addItem("GMT+08:00 - Asia/Shanghai", "Asia/Shanghai");
        timeZones.addItem("GMT+08:00 - Asia/Singapore", "Asia/Singapore");
        timeZones.addItem("GMT+08:00 - Asia/Taipei", "Asia/Taipei");
        timeZones.addItem("GMT+08:00 - Asia/Ulaanbaatar", "Asia/Ulaanbaatar");
        timeZones.addItem("GMT+08:00 - Asia/Urumqi", "Asia/Urumqi");
        timeZones.addItem("GMT+08:00 - Australia/Perth", "Australia/Perth");
        timeZones.addItem("GMT+08:00 - Etc/GMT-8", "Etc/GMT-8");
        timeZones.addItem("GMT+08:45 - Australia/Eucla", "Australia/Eucla");
        timeZones.addItem("GMT+09:00 - Asia/Dili", "Asia/Dili");
        timeZones.addItem("GMT+09:00 - Asia/Jayapura", "Asia/Jayapura");
        timeZones.addItem("GMT+09:00 - Asia/Pyongyang", "Asia/Pyongyang");
        timeZones.addItem("GMT+09:00 - Asia/Seoul", "Asia/Seoul");
        timeZones.addItem("GMT+09:00 - Asia/Tokyo", "Asia/Tokyo");
        timeZones.addItem("GMT+09:00 - Asia/Yakutsk", "Asia/Yakutsk");
        timeZones.addItem("GMT+09:00 - Etc/GMT-9", "Etc/GMT-9");
        timeZones.addItem("GMT+09:00 - Pacific/Palau", "Pacific/Palau");
        timeZones.addItem("GMT+09:30 - Australia/Adelaide", "Australia/Adelaide");
        timeZones.addItem("GMT+09:30 - Australia/Broken_Hill", "Australia/Broken_Hill");
        timeZones.addItem("GMT+09:30 - Australia/Darwin", "Australia/Darwin");
        timeZones.addItem("GMT+10:00 - Antarctica/DumontDUrville", "Antarctica/DumontDUrville");
        timeZones.addItem("GMT+10:00 - Asia/Sakhalin", "Asia/Sakhalin");
        timeZones.addItem("GMT+10:00 - Asia/Vladivostok", "Asia/Vladivostok");
        timeZones.addItem("GMT+10:00 - Australia/Brisbane", "Australia/Brisbane");
        timeZones.addItem("GMT+10:00 - Australia/Currie", "Australia/Currie");
        timeZones.addItem("GMT+10:00 - Australia/Hobart", "Australia/Hobart");
        timeZones.addItem("GMT+10:00 - Australia/Lindeman", "Australia/Lindeman");
        timeZones.addItem("GMT+10:00 - Australia/Melbourne", "Australia/Melbourne");
        timeZones.addItem("GMT+10:00 - Australia/Sydney", "Australia/Sydney");
        timeZones.addItem("GMT+10:00 - Etc/GMT-10", "Etc/GMT-10");
        timeZones.addItem("GMT+10:00 - Pacific/Chuuk", "Pacific/Chuuk");
        timeZones.addItem("GMT+10:00 - Pacific/Guam", "Pacific/Guam");
        timeZones.addItem("GMT+10:00 - Pacific/Port_Moresby", "Pacific/Port_Moresby");
        timeZones.addItem("GMT+10:00 - Pacific/Saipan", "Pacific/Saipan");
        timeZones.addItem("GMT+10:30 - Australia/Lord_Howe", "Australia/Lord_Howe");
        timeZones.addItem("GMT+11:00 - Antarctica/Macquarie", "Antarctica/Macquarie");
        timeZones.addItem("GMT+11:00 - Asia/Anadyr", "Asia/Anadyr");
        timeZones.addItem("GMT+11:00 - Asia/Kamchatka", "Asia/Kamchatka");
        timeZones.addItem("GMT+11:00 - Asia/Magadan", "Asia/Magadan");
        timeZones.addItem("GMT+11:00 - Etc/GMT-11", "Etc/GMT-11");
        timeZones.addItem("GMT+11:00 - Pacific/Efate", "Pacific/Efate");
        timeZones.addItem("GMT+11:00 - Pacific/Guadalcanal", "Pacific/Guadalcanal");
        timeZones.addItem("GMT+11:00 - Pacific/Kosrae", "Pacific/Kosrae");
        timeZones.addItem("GMT+11:00 - Pacific/Noumea", "Pacific/Noumea");
        timeZones.addItem("GMT+11:00 - Pacific/Pohnpei", "Pacific/Pohnpei");
        timeZones.addItem("GMT+11:30 - Pacific/Norfolk", "Pacific/Norfolk");
        timeZones.addItem("GMT+12:00 - Antarctica/McMurdo", "Antarctica/McMurdo");
        timeZones.addItem("GMT+12:00 - Etc/GMT-12", "Etc/GMT-12");
        timeZones.addItem("GMT+12:00 - Pacific/Auckland", "Pacific/Auckland");
        timeZones.addItem("GMT+12:00 - Pacific/Fiji", "Pacific/Fiji");
        timeZones.addItem("GMT+12:00 - Pacific/Funafuti", "Pacific/Funafuti");
        timeZones.addItem("GMT+12:00 - Pacific/Kwajalein", "Pacific/Kwajalein");
        timeZones.addItem("GMT+12:00 - Pacific/Majuro", "Pacific/Majuro");
        timeZones.addItem("GMT+12:00 - Pacific/Nauru", "Pacific/Nauru");
        timeZones.addItem("GMT+12:00 - Pacific/Tarawa", "Pacific/Tarawa");
        timeZones.addItem("GMT+12:00 - Pacific/Wake", "Pacific/Wake");
        timeZones.addItem("GMT+12:00 - Pacific/Wallis", "Pacific/Wallis");
        timeZones.addItem("GMT+12:45 - Pacific/Chatham", "Pacific/Chatham");
        timeZones.addItem("GMT+13:00 - Etc/GMT-13", "Etc/GMT-13");
        timeZones.addItem("GMT+13:00 - Pacific/Enderbury", "Pacific/Enderbury");
        timeZones.addItem("GMT+13:00 - Pacific/Tongatapu", "Pacific/Tongatapu");
        timeZones.addItem("GMT+14:00 - Etc/GMT-14", "Etc/GMT-14");
        timeZones.addItem("GMT+14:00 - Pacific/Kiritimati", "Pacific/Kiritimati");

        return timeZones;
    }
}
