DROP TABLE IF EXISTS Tarifs, Entree, Colis, Employe, Lieu, Client;

CREATE TABLE Client (
  idClient serial primary key,
  email text not null unique,
  password text);

CREATE TABLE Lieu (
  idLieu serial primary key,
  adresse text not null,
  ouvertPublic boolean not null default false);

CREATE TABLE Employe (
  idEmploye serial primary key,
  nom text not null);

CREATE TABLE Colis (
  noColis serial primary key,
  longueur int not null,
  largeur int not null,
  hauteur int not null,
  poids real not null,
  creation timestamp not null default current_timestamp,
  livraison timestamp,
  expediteur int not null references Client,
  destinataire int not null references Client,
  pointDestination int references Lieu,
  livreur int references Employe,
  prix numeric(10,2) not null);

CREATE TABLE Entree (
  noColis int references Colis,
  dateEntree timestamp default current_timestamp,
  primary key (noColis, dateEntree),
  idLieu int references Lieu,
  idEmploye int references Employe
  );

CREATE TABLE Tarifs (
  poidsMini real,
  tailleMini int,
  date date,
  primary key (poidsMini, tailleMini, date),
  prix numeric (10, 2));

insert into Tarifs values
  (1, 50, '2013-1-1', 5),
  (2, 70, '2013-1-1', 7),
  (5, 100, '2013-1-1', 10),
  (10, 200, '2013-1-1', 15);

-- Cette fonction renvoie la somme des deux plus grands éléments du tableau
-- passé en argument
create or replace function taille(dimensions int[]) returns int as $$
declare
  sorted int[] := (select array(select unnest(dimensions) order by unnest desc));
begin
  return (select sum(unnest) from unnest(sorted[0:2]));
end;
$$ language plpgsql;

-- Renvoie le tarif en vigueur le plus petit correspondant aux contraintes
-- de taille et de poids.
create or replace function tarif(dimensions int[], poids real) returns numeric as $$
declare
  L int := taille(dimensions);
begin
  return (
    select prix from Tarifs
    where date <= current_date and poids <= poidsMini and L <= tailleMini
    order by date desc, prix asc
    limit 1);
end;
$$ language plpgsql;

-- Ce trigger remplit automatiquement le champ "tarif" si celui ci est
-- null au moment de l'insertion
create or replace function TarifDefault() returns trigger as $$
begin
  if (new.prix is null)
  then
    new.prix = tarif(array[new.longueur, new.largeur, new.hauteur], new.poids);
  end if;
  return new;
end;
$$ language plpgsql;

create trigger TarifDefaultTrigger
before insert on Colis
for each row execute procedure TarifDefault();

-- Ce trigger contrôle qu'une nouvelle entrée pour un colis est
-- bien plus récente que les précédentes
create or replace function ControlDateEntree() returns trigger as $$
begin
  if exists(select 1 from Entree where dateEntree >= new.dateEntree and noColis = new.noColis)
  then
    raise exception 'Une entrée plus récente pour le colis % existe déjà', new.noColis;
  end if;
  return new;
end;
$$ language plpgsql;

create trigger ControlDateEntreeTrigger
before insert on Entree
for each row execute procedure ControlDateEntree();


-- Ce trigger contrôle qu'aucune entrée ne peut être insérée
-- pour un colis déjà livré
create or replace function ControlLivraison() returns trigger as $$
begin
  if exists(select 1 from Colis where noColis = new.noColis and livraison is not null)
  then
    raise exception 'Le colis % est déjà livré', new.noColis;
  end if;
  return new;
end;
$$ language plpgsql;

create trigger ControlLivraisonTrigger
before insert on Entree
for each row execute procedure ControlLivraison();

