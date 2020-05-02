local t = redis.call('ZREVRANGE',KEYS[1],0,ARGV[1])
if (t~=nil) then
    redis.call('ZREMRANGEBYRANK',KEYS[1],0,ARGV[1])
end
return t