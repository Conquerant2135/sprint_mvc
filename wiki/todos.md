# A faire sur sprint 7

+ Faire une classe Java wrapper de Jackson `ObjectWriter` pour permettre de le parametrer beaucoup plus facilement (JSONconverter ou je ne sais pas)
+ Refactoriser la classe MethodMapping

## Reflechir a comment faire la partie recuperation type primitif et objet simple

+ d'abord faire une association nom parametre sur la fonction -> nom parametre de requette
  + recuperer valeur -> req.getParameter(parameter.getName) dans ce case
  + parser la valeur suivant le type du parametre en question
  + mettre nen parametre de la methode qui appelle tout ces trucs
+ ajouter un RequestParam pour designer le parametre (comme ca on n'est plus figee dans nom parametre)



## Reflechir a commenter implementer la partie creation d'objet , recuperation de DTO et tout ca
