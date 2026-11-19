select firstname,lastname,email,location,batch_number,name
from users u
       inner join campus c on u.campus_id=c.id
       inner join team t on t.id=u.team_id
where u.id=21774;