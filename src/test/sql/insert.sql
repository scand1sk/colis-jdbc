insert into Client values
  (-1, 'toto@mail.com', 'azerty'),
  (-2, 'titi@mail.com', null);

insert into Lieu values
  (-1, 'Valenciennes'),
  (-2, 'Maubeuge'),
  (-3, 'Centre de tri Nord');

insert into Employe values
  (-1, 'Roger'),
  (-2, 'Gérard'),
  (-3, 'Jean-Michel');

insert into Colis values
  (-1, 30, 20, 60, 0.8, '2018-6-2 19:32', null, -1, -2, -2, null, default);

insert into Entree values
  (-1, '2018-6-3 10:20', -1, -1),
  (-1, '2018-6-3 18:00', -3, -2),
  (-1, '2018-6-4 9:00', -2, -2);

update Colis set livraison = '2018-6-4 11:32', livreur = -3 where noColis = -1;
