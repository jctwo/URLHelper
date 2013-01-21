require 'mod1'

s = ""
for k,v in pairs(_G)
do
   s = s .. ' ' .. k
end
return s .. mod1.test()