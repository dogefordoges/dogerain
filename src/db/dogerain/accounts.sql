-- A :result value of :n below will return affected rows:
-- :name insert-account :! :n
-- :doc Insert a single account
insert into accounts (username, password, longitude, latitude, public_key, private_key)
values (:username, :password, :longitude, :latitude, :public_key, :private_key)

-- :name get-accounts :*
-- :doc Get character by id
select * from accounts