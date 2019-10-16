# Objectifs du Kata

## Refacto l'application pour valider [les 12 factors](https://12factor.net/fr)

- Renvoyer les logs vers sortie standard ou la sortie d’erreur 
- Externaliser la configuration (utiliser les variables env, …) 
- livrer un jar simple

Hors Kata : 
- Passer tout les services tiers sur des URL 
- Mettre chaque livrable dans une base de code indépendante
- Passer toutes les tâches de maintenance en worker indépendant

## Choisir une cible cloud

- Quel fournisseur ?
    1. AWS
    1. Azure
    1. Google CP
    1. IBM CP
    1. Oracle CP
    1. OVH Cloud

- Quel niveau de service ?

    - un jar déployé sur une VM statique
    - un jar managé par Beanstalk
    - un conteneur déployé sur une VM statique
    - un conteneur managé par beanstalk
    - un conteneur déployé dans ECS (conteneurs managés)
    - un conteneur déployé dans EKS (Kubernetes managé)

