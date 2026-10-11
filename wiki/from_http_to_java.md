On part de ca

```java

@UrlMapping(path = "/test" , method = HTTPMethod.POST)
public void batchProcessing(@RequestBody List<Personnes> Personnes){
    ...
}

```

On veut donc recuperer une liste de personne et une personne se decompose ainsi :

```java

public class Personne {
    String nom;
    LocalDate dateNaissance;
    Personne[] connaissances;
}

```

Pour faire le process un champ de formulaire doit ressembler a ceci .

input : name = personnes[xxx].nom 
input : name = personnes[xxx].adresse ...
input : name = personnes[xxx].connaissances[xxx]. ....

Pour relier , il est oblige d'avoir le meme nom de parametre que le truc dans le formulaire pour que ca marche effectivement

Pour un objet simple donc on doit faire :

nom
adresse
connaissances[xxx] ....

je pense que le nom de parametre doit quand meme etre obligatoire sous peine de devenir problematique 

Le code lui il va recevoir ce traitement et il va le traiter ainsi 

Il va recuperer le nom de parametres si c'est une liste

personnes il sait que c'est une liste donc il va coller 

personnes[0] puis il va avoir une carte de cet objet a partir d'un objet template ou d'une classe et il va en deduire les noms des attributs ainsi 

personnes[0] il va placer le premier point en faisant ceci . premier attribut qu'il voit donc personnes[0].nom on va dire , scalaire ? on va donc recuperer la valeur 

Puis il va descendre dans adresse , maintenant dans adresse il va essayer de recuperer les attributs ainsi personnes[0].adresse.nom (dans le httpRequest) puis il va recuperer les valeurs associes , ensuite il va recuperer les valeurs associes ou bien descendre recurcivement jusqu'a avoir tout ce qui lui faut pour former son objet et ainsi de suite

Enfin quand tout cela sera fait on aura l'objet mais le probleme maintenant c'est comment detecter si une key manque c'est a dire on a pas de personnes[0] car la personne a skip dans le javascript ce qui est un tres grand probleme et aussi comment arreter le programme si on a toutes les cles .

Pour le premier je pense qu'il faut recuperer la map et tester avec des regex si il y a quelque chose qui contains personnes[i] (ou tout autre test si c'est vraiment present )

Ensuite pour le probleme de savoir combien de truc il y a je pense qu'on ne peut pas y faire grand chose 


Pour trouver quels index d'objet sont la on va faire ceci , on va prendre toutes les clefs de getParameterMap et on va faire ceci , on va former une liste de string a partir du niveau 0 de la chose on va split suivant "." et on va faire un Set<Integer> et apres on a qu'a juste compter combien d'element unique on a et quels sont les index present et le tour est joue d'avance pour faire la partie si ce sont des tableaux internes dans des objets on va faire la meme chose mais on va couper a partir du niveau on la collection se on doit faire un split mais je ne sais pas split a partir de quoi ou bien juste on recuperer les n premiers niveaux des choses et on va etablir ce qu'on peut faire a partir de la .