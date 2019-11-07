package gen_codfis;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class codfis {

 public static void func_sel(Connection conn) throws SQLException {

  System.out.println("=== Vista del DB ===");

  PreparedStatement preparedStatement = null; //inizializzare sempre con update o insert
  ResultSet rs = null; //serve solo nelle select

  String selectSQL = "SELECT identificativo, cognome, nome, sesso, luogo_nascita, provincia, data_nasc FROM persona";


  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  while (rs.next()) {
   String identificativo = rs.getString("identificativo");
   String cognome = rs.getString("cognome");
   String nome = rs.getString("nome");
   String sesso = rs.getString("sesso");
   String luogo_nascita = rs.getString("luogo_nascita");
   String provincia = rs.getString("provincia");
   String data_nasc = rs.getString("data_nasc");

   System.out.println("Utente (" + identificativo + "): " + cognome + " " + nome + " " + " (" + sesso + "), nato in " + luogo_nascita + " (" + provincia + ") il " + data_nasc);
  }

  rs.close();
 }

 public static void func_ins(Connection conn) throws SQLException {
  Scanner in = new Scanner(System.in);
  System.out.println(" === Inserimento nuova persona in db ==== ");

  System.out.println("> Cognome: ");
  String cognome = in .next();

  System.out.println("> Nome: ");
  String nome = in .next();

  System.out.println("> Sesso: ");
  String sesso = in .next();

  System.out.println(">Luogo di nascita: ");
  String luogo_nascita = in .next();

  System.out.println(">Provincia: ");
  String provincia = in .next();

  System.out.println(">Data nascita ('yyyy'-'mm'--'dd'): ");
  String data_nasc = in .next();

  PreparedStatement preparedStatement = null;

  String sql_command = "INSERT INTO persona VALUES(nextval('id_sequence'), '" + cognome + "', '" + nome + "', '" + sesso + "', '" + luogo_nascita + "', '" + provincia + "', '" + data_nasc + "' )";

  preparedStatement = conn.prepareStatement(sql_command);
  preparedStatement.execute();

 }

 public static void func_ins_codfiscale(Connection conn, String identificativo, String fiscal_code) throws SQLException {

  PreparedStatement preparedStatement = null;

  String sql_command = "UPDATE persona SET codicefiscale = '" + fiscal_code + "' WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(sql_command);
  preparedStatement.executeUpdate();

  System.out.println("Inserito codice fiscale " + fiscal_code + " all'utente id " + identificativo);
 }

 public static String selez_id() {
  Scanner in = new Scanner(System.in);
  //System.out.println(" === Seleziona utente tramite IDENTIFICATIVO ==== ");

  System.out.println(">> ID utente: ");
  String id = in .next();

  //non devo chiuderlo mai perché altrimenti mi da la in.close e non 
  //posso farlo perché altrimenti mi chiude il System.in e mi da errore
  //in.close();

  return id;
 }

 public static void menu(Connection conn, String identificativo) throws SQLException {
  Scanner in = new Scanner(System.in);

  String answ = in .next();

  if (answ.equals("si")) {

   String base = codfisc_base(conn, identificativo);
   String codifica = calcolaCarattereDiControllo(base);

   String completo = "";

   completo = completo.concat(base).concat(codifica);

   System.out.println("Codice fiscale generato " + completo);

   System.out.println("> Inserimento in db...");

   func_ins_codfiscale(conn, identificativo, completo);

   System.out.println("Query completata!");


  } else if (answ.equals("no")) {
   System.out.println("No..");
  } else {
   System.out.println("Hai sbagliato!");
   //calcola_codfisc(conn);
  }

 }

 public static String codfisc_base(Connection conn, String identificativo) throws SQLException {
  String concat = "";

  String cognome = calc_cognome(conn, identificativo);
  String nome = calc_nome(conn, identificativo);
  String anno = calc_anno(conn, identificativo);
  String mese = calc_mese(conn, identificativo);
  String giorno = calc_giorno(conn, identificativo);
  String comune = calc_comune(conn, identificativo);

  concat = concat.concat(cognome).concat(nome).concat(anno).concat(mese).concat(giorno).concat(comune);

  //System.out.println("COD FIS PROVV: " + concat);

  return concat;
 }

 private static String calcolaCarattereDiControllo(String codice) {

  //Passaggio 1 (suddivisione dispari e pari)
  String pari = utilsParole.getStringaPari(codice);
  String dispari = utilsParole.getStringaDispari(codice);

  //Passaggio 2 (conversione valori)
  int sommaDispari = conversioneCaratteriDispari(dispari);
  int sommaPari = conversioneCaratteriPari(pari);

  //Passaggio 3 (somma, divisione e conversione finale)
  int somma = sommaDispari + sommaPari;
  int resto = (int) somma % 26;
  char restoConvertito = conversioneResto(resto);

  return Character.toString(restoConvertito);
 }

 private static int conversioneCaratteriDispari(String string) {
  int risultato = 0;
  for (int i = 0; i < string.length(); i++) {
   char carattere = string.charAt(i);
   switch (carattere) {
    case '0':
    case 'A':
     risultato += 1;
     break;
    case '1':
    case 'B':
     risultato += 0;
     break;
    case '2':
    case 'C':
     risultato += 5;
     break;
    case '3':
    case 'D':
     risultato += 7;
     break;
    case '4':
    case 'E':
     risultato += 9;
     break;
    case '5':
    case 'F':
     risultato += 13;
     break;
    case '6':
    case 'G':
     risultato += 15;
     break;
    case '7':
    case 'H':
     risultato += 17;
     break;
    case '8':
    case 'I':
     risultato += 19;
     break;
    case '9':
    case 'J':
     risultato += 21;
     break;
    case 'K':
     risultato += 2;
     break;
    case 'L':
     risultato += 4;
     break;
    case 'M':
     risultato += 18;
     break;
    case 'N':
     risultato += 20;
     break;
    case 'O':
     risultato += 11;
     break;
    case 'P':
     risultato += 3;
     break;
    case 'Q':
     risultato += 6;
     break;
    case 'R':
     risultato += 8;
     break;
    case 'S':
     risultato += 12;
     break;
    case 'T':
     risultato += 14;
     break;
    case 'U':
     risultato += 16;
     break;
    case 'V':
     risultato += 10;
     break;
    case 'W':
     risultato += 22;
     break;
    case 'X':
     risultato += 25;
     break;
    case 'Y':
     risultato += 24;
     break;
    case 'Z':
     risultato += 23;
     break;
   }
  }
  return risultato;
 }

 private static int conversioneCaratteriPari(String string) {
  int risultato = 0;
  for (int i = 0; i < string.length(); i++) {
   char carattere = string.charAt(i);
   int numero = Character.getNumericValue(carattere);

   if (Character.isLetter(carattere)) {
    //Se è una lettera
    numero = carattere - 65;
    risultato += numero;
   } else {
    //Se è un numero
    risultato += numero;
   }

  }
  return risultato;
 }

 private static char conversioneResto(int resto) {
  return (char)(resto + 65);
 }

 public static String calc_cognome(Connection conn, String identificativo) throws SQLException {
  PreparedStatement preparedStatement = null;
  ResultSet rs = null;

  String selectSQL = "SELECT cognome FROM persona WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  String cognome = null;

  while (rs.next()) {
   cognome = rs.getString("cognome");

  }

  String codiceCognome;
  int numeroConsonanti;
  cognome = utilsParole.eliminaSpaziBianchi(cognome).toUpperCase();

  if (cognome.length() >= 3) {
   numeroConsonanti = utilsParole.getNumeroConsonanti(cognome);

   if (numeroConsonanti >= 3) {
    codiceCognome = utilsParole.getPrimeConsonanti(cognome, 3);
   } else {
    codiceCognome = utilsParole.getPrimeConsonanti(cognome, numeroConsonanti);
    codiceCognome += utilsParole.getPrimeVocali(cognome, 3 - numeroConsonanti);
   }
  } else {
   System.out.println("Cognome < 3");
   int numeroCaratteri = cognome.length();
   codiceCognome = cognome + utilsParole.nXChar(3 - numeroCaratteri);
  }

  //System.out.println("Cognome: " + cognome + " --> " + codiceCognome);

  return codiceCognome;
 }

 public static String calc_nome(Connection conn, String identificativo) throws SQLException {
  PreparedStatement preparedStatement = null;
  ResultSet rs = null;

  String selectSQL = "SELECT nome FROM persona WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  String nome = null;

  while (rs.next()) {
   nome = rs.getString("nome");

  }

  String codiceNome;
  int numeroConsonanti;
  nome = utilsParole.eliminaSpaziBianchi(nome).toUpperCase();

  if (nome.length() >= 3) {

   numeroConsonanti = utilsParole.getNumeroConsonanti(nome);

   if (numeroConsonanti >= 4) {

    codiceNome = utilsParole.getConsonanteI(nome, 1) + utilsParole.getConsonanteI(nome, 3) + utilsParole.getConsonanteI(nome, 4);
   } else if (numeroConsonanti >= 3) {
    codiceNome = utilsParole.getPrimeConsonanti(nome, 3);
   } else {
    codiceNome = utilsParole.getPrimeConsonanti(nome, numeroConsonanti);
    codiceNome += utilsParole.getPrimeVocali(nome, 3 - numeroConsonanti);
   }
  } else {
   int numeroCaratteri = nome.length();
   codiceNome = nome + utilsParole.nXChar(3 - numeroCaratteri);
  }

  //System.out.println("Nome: " + nome + " --> " +codiceNome);

  return codiceNome;

 }

 public static String calc_comune(Connection conn, String identificativo) throws SQLException {

  PreparedStatement preparedStatement = null;
  ResultSet rs = null;

  String selectSQL = "SELECT luogo_nascita, provincia FROM persona WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  String luogo_nascita = null;
  String provincia = null;

  while (rs.next()) {
   luogo_nascita = rs.getString("luogo_nascita");
   provincia = rs.getString("provincia");

  }

  //se voglio evitare errori nel DB metto tutti i comuni in maiuscolo e utilizzo la funzione luogo_nascita
  //luogo_nascita = luogo_nascita.toUpperCase();
  provincia = provincia.toUpperCase();

  //System.out.println("LN: " + luogo_nascita + " prv " + provincia);

  //La tabella contiene degli spazi all'interno dei comuni quindi ho bisogno di trimmarli
  String selectSQL2 = "SELECT codice FROM comuni WHERE rtrim(ltrim(provincia)) = '" + provincia + "' AND rtrim(ltrim(comune)) = '" + luogo_nascita + "'";

  preparedStatement = conn.prepareStatement(selectSQL2);

  rs = preparedStatement.executeQuery();

  String codifica = null;

  while (rs.next()) {
   codifica = rs.getString("codice");
  }

  rs.close();


  // System.out.println("Comune: " + luogo_nascita + " (" + provincia + ") --> " + codifica);

  return codifica;
 }

 public static String calc_anno(Connection conn, String identificativo) throws SQLException {
  PreparedStatement preparedStatement = null;
  ResultSet rs = null;

  String selectSQL = "SELECT data_nasc FROM persona WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  String data = null;

  while (rs.next()) {
   data = rs.getString("data_nasc");

  }

  String anno = data.substring(0, 4);

  rs.close();

  String annocodif = anno.substring(2, 4);

  //System.out.println("Anno: " + anno + " --> " + annocodif);

  return annocodif;
 }

 public static String calc_mese(Connection conn, String identificativo) throws SQLException {

  //System.out.println("-- Recupero il mese dell'utente con id : " + identificativo);

  PreparedStatement preparedStatement = null;
  ResultSet rs = null;

  String selectSQL = "SELECT data_nasc FROM persona WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  //considero la data come 'yyyy-mm-dd'

  String data = null;

  while (rs.next()) {
   data = rs.getString("data_nasc");

  }
  String mese = data.substring(5, 7);

  rs.close();

  String risultato = null;

  switch (mese) {
   case "01":
    risultato = "A";
    break;
   case "02":
    risultato = "B";
    break;
   case "03":
    risultato = "C";
    break;
   case "04":
    risultato = "D";
    break;
   case "05":
    risultato = "E";
    break;
   case "06":
    risultato = "H";
    break;
   case "07":
    risultato = "L";
    break;
   case "08":
    risultato = "M";
    break;
   case "09":
    risultato = "P";
    break;
   case "10":
    risultato = "R";
    break;
   case "11":
    risultato = "S";
    break;
   case "12":
    risultato = "T";
    break;
   default:
    risultato = "minchiate";
    break;
  }

  //System.out.println("Mese: " + mese + " --> " + risultato);
  return risultato;

 }

 public static String calc_giorno(Connection conn, String identificativo) throws SQLException {
  PreparedStatement preparedStatement = null;
  ResultSet rs = null;

  String selectSQL = "SELECT data_nasc, sesso FROM persona WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  String data = null;
  String sesso = null;

  while (rs.next()) {
   data = rs.getString("data_nasc");
   sesso = rs.getString("sesso");
  }

  String giorno = data.substring(8, 10);

  rs.close();



  int giorno_codif = Integer.parseInt(giorno);

  if (sesso.contentEquals("F")) {
   giorno_codif += 40;
  }

  String giornocod = String.valueOf(giorno_codif);

  //System.out.println("Giorno: " + giorno + ", sesso: " + sesso + " --> " + giorno_codif);

  return giornocod;
 }

 public static void calcola_codfisc(Connection conn, String identificativo) throws SQLException {
  System.out.println("=== Calcolo Codice Fiscale === ");

  PreparedStatement preparedStatement = null;
  ResultSet rs = null;

  String selectSQL = "SELECT identificativo, cognome, nome, sesso, luogo_nascita, provincia, data_nasc FROM persona WHERE identificativo = " + identificativo;

  preparedStatement = conn.prepareStatement(selectSQL);

  rs = preparedStatement.executeQuery();

  while (rs.next()) {

   String cognome = rs.getString("cognome");
   String nome = rs.getString("nome");
   String sesso = rs.getString("sesso");
   String luogo_nascita = rs.getString("luogo_nascita");
   String provincia = rs.getString("provincia");
   String data_nasc = rs.getString("data_nasc");

   System.out.println("> Vuoi creare il codice fiscale dell'utente " + nome + " " + cognome + "? (si | no) ");

   System.out.println(">>: ");

   menu(conn, identificativo);

  }

  rs.close();
 }


 public static void main(String[] args) throws SQLException {
  Connection conn = null;

  try {
   Class.forName("org.postgresql.Driver"); //testa che il file è importato
  } catch (ClassNotFoundException e) {
   System.out.println("Driver non trovato");
   e.printStackTrace();
   return;
  }

  try {
   String url = "jdbc:postgresql://localhost/codicefiscale"; //dopo localhost ha messo db
   Properties props = new Properties();
   props.setProperty("user", "postgres");
   props.setProperty("password", "admin");
   props.setProperty("ssl", "false");
   conn = DriverManager.getConnection(url, props);


  } catch (SQLException e) {
   System.out.println("Conn fallita");
   e.printStackTrace();
   return;
  }

  Scanner in = new Scanner(System.in);

  System.out.println(" === Menu codice fiscale === ");
  System.out.println("1 Visualizza il db");
  System.out.println("2 Per inserimento utente");
  System.out.println("3 Generazione codicefiscale");
  System.out.println("0. Esci");


  boolean quit = false;

  int menuItem;

  do {
   System.out.print(">> : ");
   menuItem = in .nextInt();
   switch (menuItem) {
    case 1:
     func_sel(conn);
     break;

    case 2:
     func_ins(conn);
     break;

    case 3:
     func_sel(conn);
     String temp = null;
     temp = selez_id();
     calcola_codfisc(conn, temp);
     break;
    case 0:
     quit = true;

     break;

    default:

     System.out.println("Invalid choice.");

   }

  } while (!quit);

  System.out.println("Ciao ciao beddo!");
  System.out.println(" >> Written by Danilo Leocata ");
 }
}