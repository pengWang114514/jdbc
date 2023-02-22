	//Mustapha Daghour
	import java.sql.*;
	import java.util.ArrayList;
	import java.util.Scanner;
	class Acteur{
		private int numAct;
		private String nom;
		private String prenom;
		
		Acteur(int num, String n, String p){
			numAct = num;
			nom = n;
			prenom = p;
		}
		
		String getNom() {
			return nom;
		}
		
		String getPrenom() {
			return prenom;
		}
	}

	class Film{
		private int numFilm;
		private String titre;
		
		Film(int num, String t){
			this.numFilm = num;
			this.titre = t;
		}
		
		String getTitre() {
			return titre;
		}
	}
	public class OutilsJDBC {

	public static Connection openConnection (String url) {
	Connection co=null;
	try {
	Class.forName("oracle.jdbc.driver.OracleDriver");
	co= DriverManager.getConnection(url);
	}
	catch (ClassNotFoundException e){
	System.out.println("il manque le driver oracle");
	System.exit(1);
	}
	catch (SQLException e) {
	System.out.println("impossible de se connecter à l'url : "+url);
	System.exit(1);
	}
	return co;
	}
	public static ResultSet exec1Requete (String requete, Connection co, int type){
	ResultSet res=null;
	try {
	Statement st;
	if (type==0){
	st=co.createStatement();}
	else {
	st=co.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	ResultSet.CONCUR_READ_ONLY);
	};
	res= st.executeQuery(requete);
	}
	catch (SQLException e){
	System.out.println("Problème lors de l'exécution de la requete : "+requete);
	};
	return res;
	}
	public static void closeConnection(Connection co){
	try {
	co.close();
	System.out.println("Connexion fermée!");
	}
	catch (SQLException e) {
	System.out.println("Impossible de fermer la connexion");
	}
	}
	public static void main(String[] args){
		
		
		String url = "jdbc:oracle:thin:login/mdp@oracle.iut-orsay.fr:1521:etudom";
		Connection co = OutilsJDBC.openConnection(url);
		
		String req = "SELECT * from ens2004.Exemplaire";
		
		ResultSet res = OutilsJDBC.exec1Requete(req, co, 1);
		try {
			ResultSetMetaData rsmd = res.getMetaData();
			int numberOfColumns = rsmd.getColumnCount ( );
			for (int i = 1; i <= numberOfColumns; i++) {
				String column = rsmd.getColumnName(i); 
				String estNull;
				if (rsmd.isNullable(i)==1) {
					estNull = "et peut etre null.";
				} else {
					estNull = "et doit etre value.";
				}
				String name = rsmd.getColumnTypeName (i);
				System.out.println("L'attribut " + column + " est du type " + name + " " + estNull);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//ListeFilm("FONDA",co);
		//nbFilm("FONDA",co);
		//nbFilmReal("FONDA",co);
		//unTitre("FONDA",co);
		uneComedie("FONDA",co);
	}; 
	public static void ListeFilm(String nom,Connection co)
	{
		//QUESTION 2
		try {
			PreparedStatement psm = co.prepareStatement ("SELECT NomIndividu,PrenomIndividu,titre FROM ens2004.Film NATURAL JOIN ens2004.Acteur NATURAL JOIN ens2004.Individu WHERE nomIndividu = ? ORDER BY prenomIndividu");
			psm.setString(1,nom);
			ResultSet resultat=psm.executeQuery();
			while (resultat.next()){
				String nomI = resultat.getString(1);
				String prenomI =resultat.getString(2);
				String titreI =resultat.getString(3);
				System.out.println(nomI+" "+prenomI+" "+titreI+" ");
			}
		}catch(SQLException e) {
			
		}
	}
	public static void nbFilm(String nom,Connection co)
	{
		//QUESTION 3
		try {
			PreparedStatement psm = co.prepareStatement ("SELECT NumIndividu from ens2004.Individu where NomIndividu=?");
			psm.setString(1,nom);
			ResultSet resultat=psm.executeQuery();
			while (resultat.next())
			{
			int n=resultat.getInt(1);
			CallableStatement cst =co.prepareCall("{?= call nbreFilms1(? )}");
			cst.setInt(2, n);
			cst.registerOutParameter(1, java.sql.Types.NUMERIC);
			cst.execute();
			int result =cst.getInt(1);
			System.out.println(result);
			cst.close();
			}
			
		}catch(SQLException e) {
			
		}
	}
	/*CREATE or replace FUNCTION nbreFilms1(n ens2004.Individu.NumIndividu%type)
	return number is nbFilm number;
	begin 
	SELECT COUNT(*) into nbFilm
	from ens2004.Individu 
	natural join ens2004.Acteur a
	inner join ens2004.Film f
	on(a.numFilm=f.numfilm)
	where numIndividu =n;

	return nbFilm;
	end;*/
	
	
	//QUESTION 4
	/*

	CREATE OR REPLACE FUNCTION nbre2Film(n ens2004.Individu.nomIndividu%type)

	return number is nbFilm number;

	begin 
	select count(*) into nbFilm
	from ens2004.Individu i
	inner join ens2004.Film f
	on(i.numIndividu=f.realisateur)
	where nomIndividu=n;
	return nbFilm;
	end;
	*/

	public static void nbFilmReal(String nom,Connection co)
	{
		
		try {
			
			CallableStatement cst =co.prepareCall("{?= call nbre2Film(? )}");
			cst.setString(2, nom);
			cst.registerOutParameter(1, java.sql.Types.VARCHAR);
			cst.execute();
			int result =cst.getInt(1);
			System.out.println(result);
			cst.close();
			
			
		}catch(SQLException e) {
			
		}
	}

	//QUESTION 5
	/* 
1)
create or replace procedure unTitre(n in ens2004.Individu.nomIndividu%type,t out ens2004.film.titre%type, p out ens2004.individu.prenomIndividu%type)as
CURSOR monCursor is(
select prenomIndividu,titre
from ens2004.Individu 
natural join ens2004.Acteur a
inner join ens2004.Film f
on(a.numFilm=f.numfilm)
where nomIndividu =n);
begin
for i in monCursor
loop

t:=i.titre;
p:=i.prenomIndividu;
end loop;
dbms_output.put_line(p||' a joue dans :'||t);
end;

2)
	 */
	public static void unTitre(String nom,Connection co)
	{
		
		try {
			
			CallableStatement cst =co.prepareCall("{call unTitre(?,?,? )}");
			cst.setString(1, nom);
			cst.registerOutParameter(2, java.sql.Types.VARCHAR);
			cst.registerOutParameter(3, java.sql.Types.VARCHAR);
			cst.execute();
			String titre =cst.getString(2);
			
			
			System.out.println(nom+" a joue dans : "+titre);
			
			cst.close();
			
			
		}catch(SQLException e) {
			System.out.println("SQLException"+e);
			e.printStackTrace();
		}
	}
	
	//Exercice 6
	/*
1)
create or replace function uneComedie(n in ens2004.Individu.nomIndividu%type, p out ens2004.individu.prenomIndividu%type)
return ens2004.film.titre%type is titreC ens2004.film.titre%type;

CURSOR monCursor is(
select prenomIndividu,titre
from ens2004.Individu 
natural join ens2004.Acteur a
inner join ens2004.Film f
on(a.numFilm=f.numfilm)
inner join ens2004.genreFilm gf
on(f.numFilm=gf.numFilm)
inner join ens2004.genre g
on(gf.codeGenre=g.codeGenre)
where nomIndividu =n
and libelleGenre like 'COMEDIE%');
begin
for i in monCursor
loop

titreC:=i.titre;
p:=i.prenomIndividu;
end loop;
if titreC=null then
    titreC:='Ne joue dans aucune comedie';
    return titreC;
else
return titreC;
end if;
end;

 2)
	 */
	public static void uneComedie(String nom,Connection co)
	{
		
		try {
			
			CallableStatement cst =co.prepareCall("{?=call uneComedie(?,? )}");
			cst.setString(2, nom);
			cst.registerOutParameter(1, java.sql.Types.VARCHAR);
			cst.registerOutParameter(3, java.sql.Types.VARCHAR);
			cst.execute();
			String titre =cst.getString(1);
			
			
			System.out.println(nom+" a joue dans : "+titre);
			
			cst.close();
			
			
		}catch(SQLException e) {
			System.out.println("SQLException"+e);
			e.printStackTrace();
		}
	}
	//3)
/*
create or replace function uneComedie2(n in ens2004.Individu.nomIndividu%type, p out ens2004.individu.prenomIndividu%type)
return varchar is genref varchar(100);
CURSOR monCursor is(
select prenomIndividu,titre
from ens2004.Individu 
natural join ens2004.Acteur a
inner join ens2004.Film f
on(a.numFilm=f.numfilm)
where nomIndividu =n);

CURSOR monCursor2 is(
select prenomIndividu,titre,libelleGenre
from ens2004.Individu 
natural join ens2004.Acteur a
inner join ens2004.Film f
on(a.numFilm=f.numfilm)
inner join ens2004.genreFilm gf
on(f.numFilm=gf.numFilm)
inner join ens2004.genre g
on(gf.codeGenre=g.codeGenre)
where nomIndividu =n and prenomIndividu=p);

begin
for i in monCursor
loop
p:=i.prenomIndividu;
end loop;
for i in monCursor2
loop
genref:=genref+' '+i.libellegenre;
end loop;
return genref;
end;
 */
	public static void uneComedie2(String nom,Connection co)
	{
		
		try {
			
			CallableStatement cst =co.prepareCall("{?=call uneComedie2(?,? )}");
			cst.setString(2, nom);
			cst.registerOutParameter(1, java.sql.Types.VARCHAR);
			cst.registerOutParameter(3, java.sql.Types.VARCHAR);
			cst.execute();
			String genre =cst.getString(1);
			
			
			System.out.println(nom+" a joue dans : "+genre);
			
			cst.close();
			
			
		}catch(SQLException e) {
			System.out.println("SQLException"+e);
			e.printStackTrace();
		}
	}
	
}

