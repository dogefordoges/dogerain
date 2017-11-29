-- :name create-accounts-table
-- :command :execute
-- :result :raw
-- :doc Create accounts table
create table accounts (
	id integer auto_increment primary key,
	username varchar(100),
	password varchar(100),
	longitude real,
	latitude real,
	public_key varchar(70),
	private_key varchar(70)
)

-- A :result value of :n below will return affected rows:
-- :name insert-account :! :n
-- :doc Insert a single account
insert into accounts (username, password, public_key, private_key)
values (:username, :password, :public_key, :private_key)

-- :name update-user-location :! :n
-- :doc Update the current location of a user
update accounts set longitude = :longitude, latitude = :latitude where username = :username

-- :name get-deposit-address :? :1
-- :doc Get deposit address by username
select (public_key) from accounts where username=:username

-- :name get-private-key :? :1
-- :doc Get private key to sign withdrawal
select (private_key) from accounts where username=:username AND password=:password

-- :name get-users-near :? :n
-- :doc Get all users within the radius of the location
select (public_key) from accounts where latitude>=(:current_lat - :radius) AND 
										latitude<=(:current_lat + :radius) AND 
										longitude>=(:current_long - :radius) AND 
										longitude<=(:current_long + :radius)

-- :name drop-accounts-table :!
-- :doc Drop accounts table if exists
drop table if exists accounts

-- :name create-storms-table
-- :command :execute
-- :result :raw
-- :doc Create storm table
create table storms (
	id integer auto_increment primary key,
	username varchar,
	duration integer,
	p1_lat real,
	p1_long real,
	p2_lat real,
	p2_long real,
	p3_lat real,
	p3_long real,
	p4_lat real,
	p4_long real,
	created_at timestamp not null default current_timestamp
)

-- :name drop-storms-table :!
-- :doc Drop storms table if exists
drop table if exists storms

-- :name insert-storm :! :n
insert into storms (username, duration, p1_lat, p1_long, p2_lat, p2_long, p3_lat, p3_long, p4_lat, p4_long) values
	(:username, :duration, :p1_lat, :p1_long, :p2_lat, :p2_long, :p3_lat, :p3_long, :p4_lat, :p4_long)

-- :name get-storms-nearby :?
-- :doc get storms that surround your current location
select (username) from storms where (p1_long <= :current_long) AND (p1_lat <= :current_lat) AND  
									(p2_long >= :current_long) AND (p2_lat <= :current_lat) AND
									(p3_long >= :current_long) AND (p3_lat >= :current_lat) AND
									(p4_long <= :current_long) AND (p4_lat >= :current_lat)	AND
									username != :username								
-- :name create-bowls-table
-- :command :execute
-- :result :raw
-- :doc Create bowls table
create table bowls (
	id integer auto_increment primary key,
	username varchar,
	duration integer,
	bowl_code varchar(64)
)

-- :name insert-bowl :! :n
-- :doc Create new bowl
insert into bowls (username, duration, bowl_code) values (:username, :duration, :bowl_code)

-- :name get-bowl :? :1
-- :doc get bowl with bowl code
select (username) from bowls where bowl_code=:bowl_code

-- :name drop-bowls-table :!
-- :doc Drop bowls table if exists
drop table if exists bowls