package br.com.mobicare.cielo.commons.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benhur.souza on 06/04/2017.
 */


public final class ValidationUtils {


    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static Pattern pattern;
    private static Matcher matcher;

    static {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    private ValidationUtils() {
    }


    public static boolean isCNPJ(String input) {
        if (input == null || input.isEmpty() || input.length() < 12) {
            return false;
        }

        String cnpj = input;
        if (!cnpj.substring(0, 1).equals("")) {
            try {
                cnpj = cnpj.replace('.', ' ');// onde há ponto coloca espaço
                cnpj = cnpj.replace('/', ' ');// onde há barra coloca espaço
                cnpj = cnpj.replace('-', ' ');// onde há  coloca espaço
                cnpj = cnpj.replaceAll(" ", "");// retira espaço
                int soma = 0, dig;
                String cnpj_calc = cnpj.substring(0, 12);

                if (cnpj.length() != 14) {
                    return false;
                }
                char[] chr_cnpj = cnpj.toCharArray();
        /* Primeira parte */
                for (int i = 0; i < 4; i++) {
                    if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                        soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                    }
                }
                for (int i = 0; i < 8; i++) {
                    if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                        soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                    }
                }
                dig = 11 - (soma % 11);
                cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
        /* Segunda parte */
                soma = 0;
                for (int i = 0; i < 5; i++) {
                    if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                        soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                    }
                }
                for (int i = 0; i < 8; i++) {
                    if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                        soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                    }
                }
                dig = 11 - (soma % 11);
                cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
                return cnpj.equals(cnpj_calc);
            } catch (Exception e) {
                return false;
            }
        }

        return false;

    }

    public static String justNumbers(String text) {
        return text.replaceAll("[\\D]", "");
    }

    public static boolean isCPF(String text) {
        if(text == null || text.isEmpty()){
            return false;
        }

        String cpf = justNumbers(text);

        if (cpf.length() != 11 ||
                ("00000000000").equals(cpf) || ("11111111111").equals(cpf) ||
                ("22222222222").equals(cpf) || ("33333333333").equals(cpf) ||
                ("44444444444").equals(cpf) || ("55555555555").equals(cpf) ||
                ("66666666666").equals(cpf) || ("77777777777").equals(cpf) ||
                ("88888888888").equals(cpf) || ("99999999999").equals(cpf) ) {
            return (false);
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);

            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            sm = 0;
            peso = 11;

            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);

            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            return ((dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10)));

        } catch (Exception e) {
            return (false);
        }
    }

    public static boolean isEmail(final String hex) {
        if(hex == null){
            return false;
        }
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {

        if (password == null || password.length() < 8 || password.length() > 12) {
            return false;
        }

        String pattern = "^[@a-zA-Z0-9]*$";
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");

        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);

        return hasLetter.find() && hasDigit.find() && password.matches(pattern);
    }

    public static boolean isValidPasswordLogin(String password) {

        if (password == null || password.length() < 6 || password.length() > 20) {
            return false;
        }

        return true;
    }

    public static boolean isValidDate(final String date) {
        if (date == null || date.length() < 10)
            return false;
        String regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(date).matches();
    }

    public static boolean isValidPhoneNumber(final String numberWithMask) {
        if (numberWithMask == null || numberWithMask.length() < 14)
            return false;
        String onlyNumbers = numberWithMask.replaceAll("\\D", "");

        String regex = "^[1-9]{2}(?:[2-8]|9[1-9])[0-9]{3}[0-9]{4}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(onlyNumbers).matches();
    }

    public static String getBase64(String toEncrypt) {
        if(toEncrypt == null || toEncrypt.isEmpty()){
            return "";
        }

        byte[] data;
        String base64 = "";
        try {
            data = toEncrypt.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return base64;
    }

}
